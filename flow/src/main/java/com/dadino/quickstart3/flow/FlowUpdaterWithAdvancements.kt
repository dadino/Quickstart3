package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal

interface FlowUpdaterWithAdvancements<FLOW : FlowWithAdvancements<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStepWithAdvancements<STATE>> : FlowUpdater<FLOW, STATE, STEP> {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.Companion.noChanges()
	}
	//Do a first pass of update, using the feature specific updater
	val earlyNext = updateForFlow(previous, event)
	val earlyState = earlyNext?.state ?: previous

	//Get the advancement caused by the event with the initial update
	val advancementWithStartingStep = earlyState.flow.advancementOnEvent(earlyState, event)
	val startingStep = advancementWithStartingStep?.first
	val advancement = advancementWithStartingStep?.second

	//Generate the steps from the early state or apply the advancement (add or remove steps to the flow)
	val midFlow = if (startingStep != null && advancement != null) earlyState.flow.applyAdvancement(startingStep, advancement) else earlyState.flow
	val midBuilder = NextBuilder<STATE>()

	//Do a second pass of updates, using the advancement and the updated flow
	if (earlyNext?.state != null || previous.flow != midFlow) {
	  //Apply the updated flow to the updated state
	  val midState = earlyState.updateWithFlow(midFlow)
	  if (midState != previous)
		midBuilder.state(midState)

	  //If the flow has no more steps, add a signal to close the flow
	  if (midFlow.hasRemainingSteps().not()) midBuilder.addSignal(CloseFlow(previous.resultOnCloseMap))

	  if (advancement != null) {
		//Update the advancement to the builder
		applyAdvancement(previous, midState, advancement, midBuilder)
	  }
	} else if (advancement != null) {
	  //Update the advancement to the builder
	  applyAdvancement(previous, earlyState, advancement, midBuilder)
	}

	midBuilder.addSignals(earlyNext?.signals ?: listOf())
	midBuilder.addEffects(earlyNext?.effects ?: listOf())

	return lateUpdatePass(midBuilder.build(), previous)
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

	if (stateAfterAdvancement != previous)
	  builder.state(stateAfterAdvancement)
	builder.addEffects(getEffectsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
	builder.addSignals(getSignalsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
  }

  fun getStateAfterAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): STATE? = null
  fun getEffectsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<SideEffect> = listOf()
  fun getSignalsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<Signal> = listOf()
}