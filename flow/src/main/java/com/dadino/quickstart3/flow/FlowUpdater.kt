package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.StartSignalsProvider
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal

interface FlowUpdater<FLOW : Flow<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStep<STATE>> : Updater<STATE>, StartSignalsProvider {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.noChanges()
	}
	//Do a first pass of update, using the feature specific updater
	val initialNext = updateForFlow(previous, event)
	val initialState = initialNext?.state ?: previous

	//Get the advancement caused by the event with the initial update
	val advancement = initialState.flow.onEvent(initialState, event)

	//Apply the advancement (add or remove steps to the flow)
	val initialFlow = initialState.flow.applyAdvancement(advancement)
	val midBuilder = NextBuilder<STATE>()

	//Do a second pass of updates, using the advancement and the updated flow
	if (initialNext?.state != null || previous.flow != initialFlow) {
	  //Apply the updated flow to the updated state
	  val stateWithFlow = initialState.updateWithFlow(initialFlow)
	  midBuilder.state(stateWithFlow)

	  //If the flow has no more steps, add a signal to close the flow
	  if (initialFlow.hasRemainingSteps().not()) midBuilder.addSignal(CloseFlow(previous.resultOnCloseMap))

	  if (advancement != null) {
		//Update the advancement to the builder
		applyAdvancement(previous, stateWithFlow, advancement, midBuilder)
	  }
	} else if (advancement != null) {
	  //Update the advancement to the builder
	  applyAdvancement(previous, initialState, advancement, midBuilder)
	}
	val midNext = midBuilder.build()
	val lateBuilder = NextBuilder<STATE>()

	//Do a third pass of updates
	var finalFlow = initialFlow
	if (midNext.state != null || previous.flow != initialFlow) {
	  //If the current step provides an advancement for the back, we update the step list, usually removing steps before the current one
	  val state = midNext.state ?: previous
	  val currentStep = state.flow.getCurrentStep()
	  val advancementForBack = currentStep?.getAdvancementForBack(state)
	  finalFlow = if (advancementForBack != null) {
		state.flow.applyBackAdvancement(currentStep, advancementForBack)
	  } else state.flow
	  lateBuilder.state(state.updateWithFlow(finalFlow))
	}

	if (previous.flow.getStepIds() != finalFlow.getStepIds() || advancement != null) {
	  //If the steps have changed, we add a signal to publish the new step list to any listener
	  val signal = PublishFlowChange(advancement = advancement, updatedFlowSteps = finalFlow.getStepIds())
	  log(previous) { "Signal for update: $signal" }
	  lateBuilder.addSignal(signal)
	}

	//Add back all the signals and effects from the initial update
	lateBuilder.addSignals(initialNext?.signals ?: listOf())
	lateBuilder.addEffects(initialNext?.effects ?: listOf())

	//Add back all the signals and effects from the mid update
	lateBuilder.addSignals(midNext.signals)
	lateBuilder.addEffects(midNext.effects)

	val finalNext = lateBuilder.build()

	//Return null if the final next has no changes
	return if (finalNext.state == null && finalNext.signals.isEmpty() && finalNext.effects.isEmpty()) null else finalNext
  }

  override fun provideAdditionalStartSignals(): List<Signal> {
	val state = getInitialMainState()
	val signal = PublishFlowChange<STATE>(advancement = null, updatedFlowSteps = state.flow.getStepIds())
	log(state) { "Signal for start: $signal" }
	return listOf(signal)
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

  fun updateForFlow(previous: STATE, event: Event): Next<STATE>?

  fun getStateAfterAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): STATE? = null
  fun getEffectsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<SideEffect> = listOf()
  fun getSignalsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<Signal> = listOf()
}