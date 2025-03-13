package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.InitializeState
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State
import timber.log.Timber

interface Updater<STATE : State> {

  fun start(): Start<STATE>
  fun update(previous: STATE, event: Event): Next<STATE>?
  fun getStatesToPropagate(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
	return updated.getStatesToPropagate(isInitialization, previous)
  }

  fun internalUpdate(previous: STATE, event: Event): Next<STATE> {
	if (canLog()) Timber.d("________________${previous.javaClass.simpleName}______________________")
	if (canLog()) Timber.d("IN: ${event.javaClass.simpleName}: $event")
	val next = if (event is InitializeState) {
	  var start = start()
	  if (this is StartEffectsProvider) {
		start = start.addEffects((this as StartEffectsProvider).provideAdditionalStartEffects())
	  }
	  if (this is StartSignalsProvider) {
		start = start.addSignals((this as StartSignalsProvider).provideAdditionalStartSignals())
	  }
	  start
	} else {
	  var nextAfterUpdate = update(previous, event)

	  if (nextAfterUpdate == null && this is OnNullAttachedUpdater<*>) {
		nextAfterUpdate = (this as OnNullAttachedUpdater<STATE>).consumeEventOnNullUpdate(previous, event)
	  }
	  if (nextAfterUpdate != null && nextAfterUpdate.state == null && nextAfterUpdate.signals.isEmpty() && nextAfterUpdate.effects.isEmpty() && this is OnNoChangesAttachedUpdater<*>) {
		nextAfterUpdate = (this as OnNoChangesAttachedUpdater<STATE>).consumeEventOnNoChangesUpdate(previous, event)
	  }

	  nextAfterUpdate ?: noChanges()
	}
	if (canLog()) Timber.d("OUT: $next")
	if (canLog()) Timber.d("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯${previous.javaClass.simpleName}¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯")
	return next
  }

  fun getInitialMainState(): STATE
  fun getInitialSubStates(): List<State> = listOf()
  fun canLog() = true
}

interface OnNoChangesAttachedUpdater<STATE : State> {
  fun consumeEventOnNoChangesUpdate(previous: STATE, event: Event): Next<STATE>?

  companion object {
	/*
	Example on how to create an implementation:
		val implementation: (CreationFlowState, Event) -> Next<CreationFlowState>? = { state, evt ->
			super<OnNoChangesAttachedUpdaterWithX>.consumeEventOnNullUpdate(state, evt)
		}
	 */
	inline fun <reified STATE : State> consumeEventOnNoChangesUpdate(previous: STATE, event: Event, implementations: List<(STATE, Event) -> Next<STATE>?>): Next<STATE>? {
	  var next: Next<STATE>? = null
	  implementations.forEach { implementation ->
		val n = next
		if (n == null || (n.state == null && n.signals.isEmpty() && n.effects.isEmpty())) {
		  next = implementation(previous, event)
		}
	  }
	  return next
	}
  }
}

interface OnNullAttachedUpdater<STATE : State> {
  fun consumeEventOnNullUpdate(previous: STATE, event: Event): Next<STATE>?

  companion object {

	/*
	Example on how to create an implementation:
		val implementation: (CreationFlowState, Event) -> Next<CreationFlowState>? = { state, evt ->
			super<OnNullAttachedUpdaterWithX>.consumeEventOnNullUpdate(state, evt)
		}
	 */
	inline fun <reified STATE : State> consumeEventOnNullUpdate(previous: STATE, event: Event, implementations: List<(STATE, Event) -> Next<STATE>?>): Next<STATE>? {
	  var next: Next<STATE>? = null
	  implementations.forEach { implementation ->
		if (next == null) {
		  next = implementation(previous, event)
		}
	  }
	  return next
	}
  }
}

interface StartEffectsProvider {
  fun provideAdditionalStartEffects(): List<SideEffect>
}

interface StartSignalsProvider {
  fun provideAdditionalStartSignals(): List<Signal>
}