package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal

interface FlowUpdaterWithStepGeneration<FLOW : FlowWithStepGeneration<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStepWithGeneration<STATE>> : FlowUpdater<FLOW, STATE, STEP> {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.Companion.noChanges()
	}
	//Do a first pass of update, using the feature specific updater
	val earlyNext = updateForFlow(previous, event)
	val earlyState = earlyNext?.state ?: previous

	//Generate the steps from the early state or apply the advancement (add or remove steps to the flow)
	val stepsGeneratedFromState = generateFlowStepsFromState(earlyState)
	val midFlow = earlyState.flow.updateFlowWithSteps(stepsGeneratedFromState)
	val midBuilder = NextBuilder<STATE>()

	//Do a second pass of updates, using the updated flow
	if (earlyNext?.state != null || previous.flow != midFlow) {
	  //Apply the updated flow to the updated state
	  val stateWithFlow = earlyState.updateWithFlow(midFlow)

	  midBuilder.state(stateWithFlow)

	  //If the flow has no more steps, add a signal to close the flow
	  if (midFlow.hasRemainingSteps().not()) midBuilder.addSignal(CloseFlow(previous.resultOnCloseMap))

	  if (previous.flow != midFlow) {
		//Update the advancement to the builder
		applyAdvancement(previous, stateWithFlow, midBuilder)
	  }
	}


	midBuilder.addSignals(earlyNext?.signals ?: listOf())
	midBuilder.addEffects(earlyNext?.effects ?: listOf())
	return lateUpdatePass(midBuilder.build(), previous)
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

	if (stateAfterAdvancement != previous)
	  builder.state(stateAfterAdvancement)
	builder.addEffects(getEffectsForUpdatedFlow(previous, stateAfterAdvancement, previous.flow.getCurrentStep(), state.flow.getCurrentStep()))
	builder.addSignals(getSignalsForUpdatedFlow(previous, stateAfterAdvancement, previous.flow.getCurrentStep(), state.flow.getCurrentStep()))
  }

  fun generateFlowStepsFromState(state: STATE): List<STEP>
  fun getStateAfterUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): STATE? = null
  fun getEffectsForUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): List<SideEffect> = listOf()
  fun getSignalsForUpdatedFlow(previous: STATE, state: STATE, previousStep: STEP?, currentStep: STEP?): List<Signal> = listOf()
}