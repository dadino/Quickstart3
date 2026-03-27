package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.StartSignalsProvider
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal

@Deprecated(
  "Use FlowUpdaterWithAdvancements for most cases, or FlowUpdaterWithStepGeneration for specific ones",
  replaceWith = ReplaceWith("FlowUpdaterWithAdvancements", imports = ["com.dadino.quickstart3.flow.FlowUpdaterWithAdvancements"])
)
interface FlowUpdater<FLOW : Flow<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStep<STATE>> : Updater<STATE>, StartSignalsProvider {

  override fun update(previous: STATE, event: Event): Next<STATE>?

  override fun provideAdditionalStartSignals(): List<Signal> {
	val state = getInitialMainState()
	val signal = PublishFlowChange<STATE>(advancement = null, updatedFlowSteps = state.flow.getStepsToPublish(state))
	log(state) { "Signal for start: $signal" }
	return listOf(signal)
  }

  fun updateForFlow(previous: STATE, event: Event): Next<STATE>?
  fun getFlowForBackEvent(state: STATE): FLOW

  fun lateUpdatePass(
	midBuilder: NextBuilder<STATE>,
	earlyFlow: FLOW,
	previous: STATE,
	earlyNext: Next<STATE>?
  ): Next<STATE>? {
	val midNext = midBuilder.build()
	val lateBuilder = NextBuilder<STATE>()

	//Do a third pass of updates
	var finalFlow = earlyFlow
	var finalState = midNext.state ?: previous
	if (midNext.state != null || previous.flow != earlyFlow) {
	  //If the current step provides an advancement for the back, we update the step list, usually removing steps before the current one
	  val state = midNext.state ?: previous
	  finalFlow = getFlowForBackEvent(state)
	  finalState = state.updateWithFlow(finalFlow)
	  lateBuilder.state(finalState)
	}

	val finalStepsToPublish = finalFlow.getStepsToPublish(finalState)
	if (previous.flow.getStepsToPublish(previous) != finalStepsToPublish) {
	  //If the steps have changed, we add a signal to publish the new step list to any listener
	  val signal = PublishFlowChange<STATE>(updatedFlowSteps = finalStepsToPublish)
	  log(previous) { "Signal for update: $signal" }
	  lateBuilder.addSignal(signal)
	}

	//Add back all the signals and effects from the initial update
	lateBuilder.addSignals(earlyNext?.signals ?: listOf())
	lateBuilder.addEffects(earlyNext?.effects ?: listOf())

	//Add back all the signals and effects from the mid update
	lateBuilder.addSignals(midNext.signals)
	lateBuilder.addEffects(midNext.effects)

	val finalNext = lateBuilder.build()

	//Return null if the final next has no changes
	return if (finalNext.state == null && finalNext.signals.isEmpty() && finalNext.effects.isEmpty()) null else finalNext
  }
}

interface FlowUpdaterWithStepGeneration<FLOW : FlowWithStepGeneration<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStepWithGeneration<STATE>> : FlowUpdater<FLOW, STATE, STEP> {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.noChanges()
	}
	//Do a first pass of update, using the feature specific updater
	val earlyNext = updateForFlow(previous, event)
	val earlyState = earlyNext?.state ?: previous

	//Generate the steps from the early state or apply the advancement (add or remove steps to the flow)
	val stepsGeneratedFromState = generateFlowStepsFromState(earlyState)
	val earlyFlow = earlyState.flow.updateFlowWithSteps(stepsGeneratedFromState)
	val midBuilder = NextBuilder<STATE>()

	midBuilder.state(earlyState)

	//Do a second pass of updates, using the updated flow
	//Apply the updated flow to the updated state
	val stateWithFlow = earlyState.updateWithFlow(earlyFlow)
	midBuilder.state(stateWithFlow)

	//If the flow has no more steps, add a signal to close the flow
	if (earlyFlow.hasRemainingSteps().not()) midBuilder.addSignal(CloseFlow(previous.resultOnCloseMap))

	if (previous.flow != earlyFlow) {
	  //Update the advancement to the builder
	  applyAdvancement(previous, stateWithFlow, midBuilder)
	}

	return lateUpdatePass(midBuilder, earlyFlow, previous, earlyNext)
  }

  override fun getFlowForBackEvent(state: STATE): FLOW {
	return state.flow
  }

  /**
   * Optionally update the initial state with the advancement
   *
   * Optionally add signals and effects based on this updated state and the advancement itself
   *
   * (e.g. add a load effect if we enter a step that requires loading)
   */
  private fun applyAdvancement(previous: STATE, state: STATE, builder: NextBuilder<STATE>) {
	val stateAfterAdvancement = getStateAfterUpdatedFlow(previous, state, previous.flow.getCurrentStep(), state.flow.getCurrentStep()) ?: state

	builder.state(stateAfterAdvancement)
	builder.addEffects(getEffectsForUpdatedFlow(previous, stateAfterAdvancement, previous.flow.getCurrentStep(), state.flow.getCurrentStep()))
	builder.addSignals(getSignalsForUpdatedFlow(previous, stateAfterAdvancement, previous.flow.getCurrentStep(), state.flow.getCurrentStep()))
  }

  fun generateFlowStepsFromState(state: STATE): List<STEP>
  fun getStateAfterUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): STATE? = null
  fun getEffectsForUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): List<SideEffect> = listOf()
  fun getSignalsForUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): List<Signal> = listOf()
}

interface FlowUpdaterWithAdvancements<FLOW : FlowWithAdvancements<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStepWithAdvancements<STATE>> : FlowUpdater<FLOW, STATE, STEP> {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.noChanges()
	}
	//Do a first pass of update, using the feature specific updater
	val earlyNext = updateForFlow(previous, event)
	val earlyState = earlyNext?.state ?: previous

	//Get the advancement caused by the event with the initial update
	val advancement = earlyState.flow.onEvent(earlyState, event)

	//Generate the steps from the early state or apply the advancement (add or remove steps to the flow)
	val earlyFlow = earlyState.flow.applyAdvancement(advancement)
	val midBuilder = NextBuilder<STATE>()

	midBuilder.state(earlyState)

	//Do a second pass of updates, using the advancement and the updated flow
	if (earlyNext?.state != null || previous.flow != earlyFlow) {
	  //Apply the updated flow to the updated state
	  val stateWithFlow = earlyState.updateWithFlow(earlyFlow)
	  midBuilder.state(stateWithFlow)

	  //If the flow has no more steps, add a signal to close the flow
	  if (earlyFlow.hasRemainingSteps().not()) midBuilder.addSignal(CloseFlow(previous.resultOnCloseMap))

	  if (advancement != null) {
		//Update the advancement to the builder
		applyAdvancement(previous, stateWithFlow, advancement, midBuilder)
	  }
	} else if (advancement != null) {
	  //Update the advancement to the builder
	  applyAdvancement(previous, earlyState, advancement, midBuilder)
	}

	return lateUpdatePass(midBuilder, earlyFlow, previous, earlyNext)
  }

  override fun getFlowForBackEvent(state: STATE): FLOW {
	val currentStep = state.flow.getCurrentStep()
	val advancementForBack = currentStep?.getAdvancementForBack(state)
	return if (advancementForBack != null) {
	  state.flow.applyBackAdvancement(currentStep, advancementForBack)
	} else state.flow
  }

  /**
   * Optionally update the initial state with the advancement
   *
   * Optionally add signals and effects based on this updated state and the advancement itself
   *
   * (e.g. add a load effect if we enter a step that requires loading)
   */
  private fun applyAdvancement(previous: STATE, state: STATE, advancement: FlowAdvancement<STATE>, builder: NextBuilder<STATE>) {
	val stateAfterAdvancement = getStateAfterAdvancement(state, previous.flow.getCurrentStep(), advancement) ?: state

	builder.state(stateAfterAdvancement)
	builder.addEffects(getEffectsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
	builder.addSignals(getSignalsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
  }

  fun getStateAfterAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): STATE? = null
  fun getEffectsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<SideEffect> = listOf()
  fun getSignalsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<Signal> = listOf()
}