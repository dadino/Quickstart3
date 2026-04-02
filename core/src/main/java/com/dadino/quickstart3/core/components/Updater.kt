package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.InitializeState
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.QuickLogger

interface UpdaterDecorator<STATE : State> {
  fun getInitialEffects(initialState: STATE): List<SideEffect> = listOf()
  fun getInitialSignals(initialState: STATE): List<Signal> = listOf()
  fun update(previous: STATE, event: Event): Next<STATE>? = null
  fun evaluateOnlyOnNullOrNoChanges(event: Event): Boolean = false
  fun skipEventEvaluation(previous: STATE, event: Event): Boolean = false
}

/**
 * An [Updater] is responsible for managing the state of a specific feature or component.  It defines
 * how the state changes in response to events.
 *
 * @param STATE The type of state managed by this updater.  Must implement the [State] interface.
 */
interface Updater<STATE : State> : UpdaterDecorator<STATE> {
  /**
   * Returns the initial state for the main screen.  This state is used when the application
   * first starts and represents the default configuration.  It typically includes empty lists,
   * default settings, or other initial values for UI elements and data.
   *
   * @return The initial state for the main screen.
   */
  fun getInitialMainState(): STATE
  override fun getInitialEffects(initialState: STATE): List<SideEffect> {
	val start = start()
	return start.effects
  }

  override fun getInitialSignals(initialState: STATE): List<Signal> {
	val start = start()
	return start.signals
  }

  fun start(): Start<STATE>

  fun getDecorators(): List<UpdaterDecorator<STATE>> = listOf()
  fun getInitialSubStates(): List<State> = listOf()
  fun getStatesToPropagate(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
	return updated.getStatesToPropagate(isInitialization, previous)
  }

  /**
   *  This function handles the core state update logic.  It takes the current state and an event as input,
   * and returns the next state, potentially with associated effects and signals.
   *
   *  It performs the following actions:
   *  1. **Logging:** If enabled via `canLog()`, it logs the incoming event and current state for debugging.
   *  2. **Initialization:** If the event is `InitializeState`, it initializes the state by calling the `start()`
   *     function. It also allows for additional start effects and signals to be added if the class implements
   *     `StartEffectsProvider` and `StartSignalsProvider` respectively.
   *  3. **Regular Update:** For other events, it delegates the update logic to the `update()` function, passing
   *     the current state and the event.
   *  4. **Null Update Handling:** If the `update()` function returns `null` (indicating the event was not handled),
   *     and the class implements `OnNullAttachedUpdater`, it allows the updater to handle the event and potentially
   *     return a new state.
   *  5. **No Changes Handling:** If the `update()` function returns a `Next` object with no changes (null state,
   *     empty effects, and signals) and the class implements `OnNoChangesAttachedUpdater`, it allows the updater
   *     to handle this situation and potentially return a new state.
   *  6. **Default Behavior:** If the event is not handled by any of the above steps, it returns a `noChanges()`
   *     result, indicating no change in state.
   *  7. **Logging:** If logging is enabled, it logs the resulting next state, effects, and signals.
   *  8. **Return:** Returns the `Next` object, which contains the new state (if any), associated effects, and signals.
   *
   * @param previous The current state of the state machine.
   * @param event The event that triggered the update.
   * @return A `Next` object representing the next state, associated effects, and signals.
   */
  fun internalUpdate(previous: STATE, event: Event): Next<STATE> {
	log(previous) { "________________${previous.javaClass.simpleName}______________________" }
	log(previous) { "IN: ${event.javaClass.simpleName}: $event" }
	val next = if (event is InitializeState) {
	  start()
	} else {
	  update(previous, event)
	}
	log(previous) { "OUT: $next" }
	log(previous) { "¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯${previous.javaClass.simpleName}¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯" }
	return next ?: noChanges()
  }

  fun canLog() = true
  private fun tag(state: STATE) = "${state::class.simpleName}Updater"
  fun log(state: STATE, message: () -> String) {
	if (canLog()) {
	  QuickLogger.tag(tag(state)).d(message)
	}
  }
}