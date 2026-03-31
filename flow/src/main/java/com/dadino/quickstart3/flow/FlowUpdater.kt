package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.StartSignalsProvider
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
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
	midNext: Next<STATE>,
	previousState: STATE
  ): Next<STATE>? {
	val lateBuilder = NextBuilder<STATE>()

	//Do a third pass of updates
	var finalState = midNext.state ?: previousState
	var finalFlow = finalState.flow
	if (midNext.state != null || previousState.flow != finalFlow) {
	  //If the current step provides an advancement for the back, we update the step list, usually removing steps before the current one
	  val state = midNext.state ?: previousState
	  finalFlow = getFlowForBackEvent(state)
	  finalState = state.updateWithFlow(finalFlow)
	  if (finalState != previousState)
		lateBuilder.state(finalState)
	}

	val finalStepsToPublish = finalFlow.getStepsToPublish(finalState)
	if (previousState.flow.getStepsToPublish(previousState) != finalStepsToPublish) {
	  //If the steps have changed, we add a signal to publish the new step list to any listener
	  val signal = PublishFlowChange<STATE>(updatedFlowSteps = finalStepsToPublish)
	  log(previousState) { "Signal for update: $signal" }
	  lateBuilder.addSignal(signal)
	}

	//Add back all the signals and effects from the midNext
	lateBuilder.addSignals(midNext.signals)
	lateBuilder.addEffects(midNext.effects)

	val finalNext = lateBuilder.build()

	//Return null if the final next has no changes
	return if ((finalNext.state == null || finalNext.state == previousState) && finalNext.signals.isEmpty() && finalNext.effects.isEmpty()) null else finalNext
  }
}

