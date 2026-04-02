package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State

data class DecoratedUpdater<STATE : State>(
  private val delegateUpdater: Updater<STATE>,
  private val decorators: List<UpdaterDecorator<STATE>> = emptyList()
) : Updater<STATE> {

  private val justDecorators by lazy { decorators + delegateUpdater.getDecorators() }
  private val allUpdaters by lazy { justDecorators + delegateUpdater }

  override fun getInitialMainState(): STATE {
	return delegateUpdater.getInitialMainState()
  }

  override fun getInitialEffects(initialState: STATE): List<SideEffect> {
	return delegateUpdater.getInitialEffects(initialState)
  }

  override fun getInitialSignals(initialState: STATE): List<Signal> {
	return delegateUpdater.getInitialSignals(initialState)
  }

  override fun start(): Start<STATE> {
	val startState = getInitialMainState()

	val allEffects = mutableListOf<SideEffect>()
	val allSignals = mutableListOf<Signal>()

	for (updater in justDecorators) {
	  allEffects.addAll(updater.getInitialEffects(startState))
	  allSignals.addAll(updater.getInitialSignals(startState))
	}

	return Start(
	  startState = startState,
	  effects = allEffects + getInitialEffects(startState),
	  signals = allSignals + getInitialSignals(startState)
	)
  }

  override fun update(previous: STATE, event: Event): Next<STATE>? {
	var accumulatedState: STATE? = null
	val allEffects = mutableListOf<SideEffect>()
	val allSignals = mutableListOf<Signal>()
	var eventHandled = false
	val updatersToEvaluateOnlyOnNullOrNoChanges = mutableListOf<UpdaterDecorator<STATE>>()

	for (updater in allUpdaters) {
	  val handled = evaluateEventWithUpdater(
		updater = updater,
		originalState = previous,
		updatedState = accumulatedState,
		event = event,
		effectAccumulator = allEffects,
		signalAccumulator = allSignals,
		updaterToEvaluateOnSecondPass = updatersToEvaluateOnlyOnNullOrNoChanges,
		isSecondPass = false,
		onStateUpdated = { accumulatedState = it })

	  if (handled) eventHandled = true
	}

	for (updater in updatersToEvaluateOnlyOnNullOrNoChanges) {
	  val handled = evaluateEventWithUpdater(
		updater = updater,
		originalState = previous,
		updatedState = accumulatedState,
		event = event,
		effectAccumulator = allEffects,
		signalAccumulator = allSignals,
		updaterToEvaluateOnSecondPass = null,
		isSecondPass = true,
		onStateUpdated = { accumulatedState = it })

	  if (handled) eventHandled = true
	}

	// If no updater handled the event, return null
	if (!eventHandled) return null

	// Only pass the accumulatedState if it's different from the previous state
	val finalState = if (accumulatedState != previous) accumulatedState else null

	return Next(
	  state = finalState,
	  effects = allEffects,
	  signals = allSignals
	)
  }

  private fun evaluateEventWithUpdater(
	updater: UpdaterDecorator<STATE>,
	originalState: STATE,
	updatedState: STATE?,
	event: Event,
	effectAccumulator: MutableList<SideEffect>,
	signalAccumulator: MutableList<Signal>,
	updaterToEvaluateOnSecondPass: MutableList<UpdaterDecorator<STATE>>?,
	isSecondPass: Boolean = false,
	onStateUpdated: (STATE) -> Unit
  ): Boolean {
	val previous = updatedState ?: originalState
	if (updater.skipEventEvaluation(previous, event)) return false

	val evaluateOnlyOnNullOrNoChanges = updater.evaluateOnlyOnNullOrNoChanges(event)
	if (evaluateOnlyOnNullOrNoChanges && !isSecondPass) {
	  updaterToEvaluateOnSecondPass?.add(updater)
	  return false
	}

	if (evaluateOnlyOnNullOrNoChanges &&
	  (updatedState != null
		  || effectAccumulator.isNotEmpty()
		  || signalAccumulator.isNotEmpty())
	) {
	  return false
	}

	val next = updater.update(previous, event)

	return if (next != null) {
	  // If this updater modified the state, carry it forward
	  if (next.state != null) {
		onStateUpdated(next.state)
	  }

	  effectAccumulator.addAll(next.effects)
	  signalAccumulator.addAll(next.signals)
	  true
	} else false
  }

  override fun getInitialSubStates(): List<State> {
	return delegateUpdater.getInitialSubStates()
  }

  override fun getStatesToPropagate(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
	return delegateUpdater.getStatesToPropagate(previous, updated, isInitialization)
  }
}