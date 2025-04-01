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

/**
 * An [Updater] is responsible for managing the state of a specific feature or component.  It defines
 * how the state changes in response to events.
 *
 * @param STATE The type of state managed by this updater.  Must implement the [State] interface.
 */
interface Updater<STATE : State> {

  fun start(): Start<STATE>
  fun update(previous: STATE, event: Event): Next<STATE>?
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

  /**
   * Returns the initial state for the main screen.  This state is used when the application
   * first starts and represents the default configuration.  It typically includes empty lists,
   * default settings, or other initial values for UI elements and data.
   *
   * @return The initial state for the main screen.
   */
  fun getInitialMainState(): STATE
  fun getInitialSubStates(): List<State> = listOf()
  fun canLog() = true
}

/**
 * This interface defines a strategy for handling events when no state changes have been applied by other handlers.
 * Implementations of this interface provide a function, [consumeEventOnNoChangesUpdate], that determines
 * how to react to an event given the current state when no other handlers have resulted in a state transition.
 *
 * This can be useful for scenarios where you want to perform actions or update the state based on an event
 * only if no other relevant changes have occurred.  For example, you might use this to implement fallback behavior
 * or ensure that certain events are always processed, even if their primary handlers don't trigger a state change.
 *
 * **Note:** The [consumeEventOnNoChangesUpdate] method will be invoked only if the primary update method of the
 * updater returns a `Next` object indicating that no changes to the state, signals, or effects are required.
 * This means that no other event handling logic within the primary update process resulted in a state transition.
 *
 * **Usage:**
 *  You typically don't directly implement this interface yourself. Instead, implementations are used internally
 *  to allow for these "no change" update handlers.  You provide implementations as lambda expressions that will be
 *  invoked when the primary update process yields no changes.
 *
 * @param STATE The type of the state managed by the state machine.  Must implement [State].
 */
interface OnNoChangesAttachedUpdater<STATE : State> {
  fun consumeEventOnNoChangesUpdate(previous: STATE, event: Event): Next<STATE>?

  companion object {
	/**
	 *  This function iterates through a list of implementations, each of which attempts to process an event
	 *  given the current state.  It returns the first non-null `Next<STATE>` result where no changes (no new state, signals, or effects) were proposed
	 *  by a previous implementation in the chain.  If a previous implementation returns a `Next` with a new state, signals, or effects, it is considered
	 *  as having handled the event, and subsequent implementations are not invoked.
	 *
	 *  This is useful for implementing a chain-of-responsibility pattern where different handlers can be tried in sequence until one successfully processes the event (or until all have been tried and none succeeded without side effects).
	 *
	 * @param previous The previous state of the flow.
	 * @param event The event to be processed.
	 * @param implementations A list of lambda expressions, each taking the previous state and the event as input,
	 *                      and returning a `Next<STATE>?` representing the next state, signals, and effects after processing the event,
	 *                      or `null` if the event is not handled by that implementation.
	 * @return A `Next<STATE>?` representing the next state, signals, and effects after processing the event, or `null` if no implementation
	 *         successfully handled the event without changes.
	 *
	 *  Example Usage:
	 *  ```kotlin
	 *  data class MyState(val count: Int) : State
	 *  sealed class MyEvent : Event {
	 *      object Increment : MyEvent()
	 *      object Decrement : MyEvent()
	 *  }
	 *
	 *  val impl1: (MyState, Event) -> Next<MyState>? = { state, event ->
	 *      when (event) {
	 *          MyEvent.Increment -> Next(MyState(state.count + 1)) // Handles increment, changes state
	 *          else -> null // Doesn't handle other events
	 *      }
	 *  }
	 *
	 *  val impl2: (MyState, Event) -> Next<MyState>? = { state, event ->
	 *      when (event) {
	 *          MyEvent.Decrement -> Next(state.copy(count = state.count -1)) //Handles decrement and changes state
	 *          else -> Next(state) //handles other events and produce a Next without */

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

/**
 * Interface for handling events when a specific attached object within the state is null.
 * This allows for centralized logic to determine the next state based on an event,
 * specifically when a monitored component within the state is in a null or uninitialized state.
 *
 * The `consumeEventOnNullUpdate` method should be implemented to define the behavior
 * when such a condition occurs.  It provides the previous state and the triggering event
 * and should return a `Next<STATE>` object representing the next state if the event should be processed
 * in this null-attached context, or `null` if the event should be handled by other logic.
 *
 * Implementations of this interface can be combined and processed sequentially using the
 * `consumeEventOnNullUpdate` companion object method, allowing for a modular and extensible
 * approach to handling null-attached scenarios.
 *
 * Example Use Case:
 * Imagine a state representing a user profile with an attached "address" object.  If the
 * address is null (e.g., not yet entered by the user), an `OnNullAttachedUpdater` could be
 * used to specifically handle events related to address entry, presenting an address form
 * or prompting the user to enter their address.  Once the address is no longer null, these
 * events would likely be handled by different parts of the application logic.
 *
 *  @param STATE The type of the state managed by the flow. Must implement the `State` interface.
 */
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

/**
 * An interface for providing additional side effects that should be executed at the start of a composable.
 *
 * This allows modules to inject custom logic (e.g., analytics, initialization) into specific
 * composables without directly modifying the composable's code. Implementations of this interface
 * will be automatically discovered and their provided side effects will be merged with the composable's
 * own side effects.
 */
interface StartEffectsProvider {
  /**
   * Provides a list of [SideEffect]s that should be applied at the start of a composable lifecycle.
   *
   * These effects are typically used for tasks like initializing resources, setting up observers,
   * or performing other actions that need to happen once when a composable becomes active.
   *
   * Example use cases:
   * - Registering an event listener
   * - Loading initial data
   * - Setting up a state observer
   *
   * @return A list of [SideEffect] instances to be applied.  Returns an empty list if no side effects are needed.
   */
  fun provideAdditionalStartEffects(): List<SideEffect>
}

/**
 * An interface for providing additional start signals to the system.
 *
 * Implementing this interface allows components to inject custom logic for determining
 * when the application or a specific module is considered "started".  This can be used to
 * supplement or override the default start signals of the system, enabling more nuanced
 * startup detection based on the specific needs of a given feature or component.
 *
 * For example, an implementation might wait for a network connection, a database to be
 * initialized, or a specific configuration to be loaded before signaling that the
 * component is ready.
 *
 * The system aggregates start signals from all registered providers to determine the
 * overall startup state.  The application or module is considered started only when all
 * provided signals are active.
 *
 * @see Signal  For the base interface representing an individual start signal.
 */
interface StartSignalsProvider {
  /**
   * Provides a list of additional signals that should be considered as start signals for a particular process or workflow.
   * These signals supplement the default start signals and can be used to trigger the process under specific circumstances
   * not covered by the standard start mechanisms.  For example, these might include signals generated by external events
   * or signals triggered by the completion of another process.
   *
   * @return A list of [Signal] objects representing the additional start signals.  An empty list indicates that there are no
   *         additional start signals beyond the default set.
   */
  fun provideAdditionalStartSignals(): List<Signal>
}