package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NextBuilder
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal

interface FlowUpdater<FLOW : Flow<FLOW, STATE, STEP>, STATE : FlowState<FLOW, STATE, STEP>, STEP : FlowStep<STATE>> : Updater<STATE> {

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	previous.getEventsToSkip().forEach { kclass ->
	  if (kclass.isInstance(event)) return Next.noChanges()
	}
	val next = updateForFlow(previous, event)
	val state = next?.state ?: previous
	val advancement = state.flow.onEvent(state, event)
	val newFlow = state.flow.applyAdvancement(advancement)
	val builder = NextBuilder<STATE>()

	if (next?.state != null || previous.flow != newFlow) {
	  val stateWithFlow = state.updateWithFlow(newFlow)
	  builder.state(stateWithFlow)
	  if (newFlow.hasRemainingSteps().not()) builder.addSignal(CloseFlow(previous.resultOnCloseMap))

	  if (advancement != null) {
		val stateAfterAdvancement = getStateAfterAdvancement(stateWithFlow, previous.flow.getCurrentStep(), advancement) ?: stateWithFlow

		builder.state(stateAfterAdvancement)
		builder.addEffects(getEffectsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
		builder.addSignals(getSignalsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
	  }
	} else if (advancement != null) {
	  val stateAfterAdvancement = getStateAfterAdvancement(state, previous.flow.getCurrentStep(), advancement) ?: state

	  builder.state(stateAfterAdvancement)
	  builder.addEffects(getEffectsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
	  builder.addSignals(getSignalsForAdvancement(stateAfterAdvancement, previous.flow.getCurrentStep(), advancement))
	}

	builder.addSignals(next?.signals ?: listOf())
	builder.addEffects(next?.effects ?: listOf())
	val n = builder.build()
	return if (n.state == null && n.signals.isEmpty() && n.effects.isEmpty()) null else
	  builder.build()
  }

  fun updateForFlow(previous: STATE, event: Event): Next<STATE>?

  fun getStateAfterAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): STATE? = null
  fun getEffectsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<SideEffect> = listOf()
  fun getSignalsForAdvancement(state: STATE, startingStep: STEP?, advancement: FlowAdvancement<STATE>): List<Signal> = listOf()
}