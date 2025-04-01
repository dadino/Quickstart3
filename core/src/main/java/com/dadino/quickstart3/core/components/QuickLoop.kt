package com.dadino.quickstart3.core.components

import androidx.annotation.VisibleForTesting
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.InitializeState
import com.dadino.quickstart3.base.NoOpEvent
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.SideEffectNotHandledException
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.toAsync
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.reflect.KClass

/**
 *  A state machine implementation for managing complex application logic.
 *
 *  `QuickLoop` orchestrates state transitions, side effect handling, and event processing within a defined loop.  It uses a unidirectional data flow pattern,
 *  reacting to `Event`s, updating its internal `State`, and emitting `Signal`s or triggering `SideEffect`s as a consequence.
 *
 *  @param STATE The primary state type for this loop, conforming to the `State` interface.
 *  @param loopName A descriptive name for the loop, primarily used for debugging and logging.
 *  @param updater An `Updater` instance responsible for determining the next state based on the current state and incoming events.  Crucially, it also defines the initial state(s).
 *  @param sideEffectHandlers A list of `SideEffectHandler` instances.  Each handler can choose to process specific `SideEffect`s triggered by state updates, potentially emitting new events as a result.
 *  @param onConnectCallback A callback invoked when the loop successfully initializes and starts processing events.
 *
 *  Key Concepts:
 *    - **State:** Represents a snapshot of the application's data at a given point in time.  `QuickLoop` manages a main state (`STATE`) and can optionally manage sub-states. All states must conform to the `State` interface.
 *    - **Event:** Represents an external stimulus that can trigger a state transition.  Events are processed sequentially.  All events must conform to the `Event` interface.
 *    - **Updater:**  A critical component that defines the state transition logic.  Its `internalUpdate` method takes the current state and an event as input, returning a `Next` object.  The `Next` object describes the new state, any signals to emit, and any side effects to handle.
 *    - **Signal:** Represents an */
class QuickLoop<STATE : State>(
	private val loopName: String,
	private val updater: Updater<STATE>,
	private val sideEffectHandlers: List<SideEffectHandler> = listOf(),
	private val onConnectCallback: OnConnectCallback

) {

	var enableLogging = false
	var canReceiveEvents = false

	private val eventSourcesCompositeDisposable = CompositeDisposable()
	private val eventSourcesMap: HashMap<String, Disposable> = hashMapOf()

	private val mainStateRelay: BehaviorRelay<STATE> by lazy { BehaviorRelay.createDefault(updater.getInitialMainState()) }
	private val stateRelayMap: Map<KClass<out State>, BehaviorRelay<State>> by lazy {
		val relays = hashMapOf<KClass<out State>, BehaviorRelay<State>>()
		relays[mainStateRelay.value!!::class] = mainStateRelay as BehaviorRelay<State>
		updater.getInitialSubStates().forEach { subState -> relays[subState::class] = BehaviorRelay.createDefault(subState) }
		relays
	}
	private val stateFlowableMap: Map<KClass<out State>, Flowable<out State>> by lazy {
		val flowables = hashMapOf<KClass<out State>, Flowable<out State>>()
		stateRelayMap.entries.forEach {
			flowables[it.key] = it.value
				.toFlowable(BackpressureStrategy.LATEST)
				.replay(1)
				.autoConnect(0)
		}
		flowables
	}

	private val signalRelay: PublishRelay<Signal> by lazy { PublishRelay.create<Signal>() }
	val signals: Flowable<Signal> by lazy {
		signalRelay.toFlowable(BackpressureStrategy.BUFFER)
	}

	private val eventRelay: PublishRelay<Event> = PublishRelay.create<Event>()

  /**
   * This disposable manages the internal reactive stream that processes events, updates the state,
   * and handles side effects.
   *
   * The stream starts with an `InitializeState` event and filters out `NoOpEvent` events.
   * It then converts the event relay to a Flowable with a buffer backpressure strategy.
   *
   * For each event, it:
   *  1. Retrieves the current state.
   *  2. Uses the [updater] to compute the next state and any associated signals or side effects.
   *  3. Propagates updated sub-states to child state machines, if necessary.
   *  4. Propagates signals to listeners, if necessary.
   *  5. Handles side effects by executing corresponding logic, if necessary.
   *  6. If the event is an initialization event (Start<STATE>):
   *     - Sets `canReceiveEvents` to true, allowing subsequent events to be processed.
   *     - Invokes the [onConnectCallback] to notify that the state machine has been initialized.
   *
   * The result of each event processing is mapped to `true` (indicating successful processing).
   * The Flowable is then converted to an async operation and subscribed, starting the event processing loop.
   */
	private val internalDisposable: Disposable = eventRelay.filter { it !is NoOpEvent }
		.toFlowable(BackpressureStrategy.BUFFER)
		.startWith(InitializeState)
		.map { event ->
			val previousState = currentState()
			val next = updater.internalUpdate(previousState, event)
			val newState = next.state ?: previousState
			val isInitialization = next is Start<STATE>
			val updatedSubStates = updater.getStatesToPropagate(previousState, newState, isInitialization)

			if (updatedSubStates.isNotEmpty()) {
				propagateStates(updatedSubStates)
			}
			if (next.signals.isNotEmpty()) {
				propagateSignals(next.signals)
			}
			if (next.effects.isNotEmpty()) {
				handleSideEffects(next.effects)
			}

			if (isInitialization) {
				canReceiveEvents = true
				onConnectCallback.onConnect()
			}

			true
		}
		.toAsync()
		.subscribe()

	fun disconnect() {
		internalDisposable.dispose()
		eventSourcesCompositeDisposable.clear()
		eventSourcesMap.clear()
		sideEffectHandlers.forEach { it.onClear() }
	}

  /**
   * Returns a list of Flowables, each emitting a stream of application states.
   *
   * This function retrieves the Flowables of application states from the internal `stateFlowableMap`.  Each entry in the map represents a different state stream, and the function extracts the `Flowable` from each entry's value.
   *
   * @return A list of Flowables, where each Flowable emits a sequence of states.  The specific type of `State` emitted by each Flowable is determined by the underlying state management implementation.
   */
	fun getStateFlows(): List<Flowable<out State>> = stateFlowableMap.entries.map { it.value }

  /**
   * Retrieves a Flowable representing the state flow for a specific sub-state class.
   *
   * This function accesses an internal map (`stateFlowableMap`) that associates sub-state classes
   * (derived from `State`) with their corresponding Flowable instances.  It is used to observe
   * changes to the application's state within a particular sub-state category.
   *
   * @param subStateClass The KClass representing the sub-state for which to retrieve the flow.  This should be a class
   *                     that inherits from the `State` interface.
   * @return A Flowable emitting the state objects for the specified sub-state.  The Flowable's
   *         elements will be instances of the `subStateClass`.
   * @throws RuntimeException if no Flowable is found for the provided `subStateClass` in the
   *                          `stateFlowableMap`, indicating that the sub-state is not registered.
   */
	fun getStateFlow(subStateClass: KClass<out State>): Flowable<out State> =
		stateFlowableMap[subStateClass] ?: throw RuntimeException("No states for class ${subStateClass.qualifiedName}")

  /**
   * Retrieves a list of the current values of all `StateFlow` instances held within the `stateRelayMap`.
   *
   * This function iterates through the entries of the `stateRelayMap`, extracting the `StateFlow` (or `MutableStateFlow`) associated with each key.
   * It then retrieves the current value (state) held by each `StateFlow` and collects these values into a list.  Any entry in the map whose value is `null` is skipped and not included in the resulting list.
   *
   * @return A list of `State` objects representing the current values of the `StateFlow`s in the `stateRelayMap`.
   */
	fun getSubStates(): List<State> = stateRelayMap.entries.mapNotNull { it.value.value }

  /**
   * Retrieves the current sub-state of a specific type.
   *
   * This function accesses a map ([stateRelayMap]) that stores sub-states associated with their respective classes.
   * It then returns the current value of the sub-state for the provided [subStateClass].
   *
   * @param subStateClass The KClass representing the type of sub-state to retrieve.  Must be a subclass of `State`.
   * @return The current sub-state of the specified type, or `null` if no sub-state of that type is currently active or if the associated relay is empty.
   */
	fun getSubState(subStateClass: KClass<out State>): State? {
		return stateRelayMap[subStateClass]?.value
	}

	fun currentState(): STATE {
		return mainStateRelay.value!!
	}

	fun receiveEvent(event: Event) {
		eventRelay.accept(event)
	}

  /**
   * Attaches an event source to the event system.
   *
   * This function subscribes to an Observable of Events, associating it with a given tag.
   * If a source with the same tag already exists, it will be ignored.  This prevents duplicate
   * subscriptions from the same source. The subscription is added to a composite disposable
   * for easy management and disposal when needed (e.g., when the event system is no longer in use).
   *
   * @param tag A unique string identifier for the event source. This is used to prevent duplicate
   *            subscriptions from the same source.
   * @param eventObservable An Observable that emits Events. The event system will subscribe to this
   *                        Observable and relay any emitted events to its listeners.
   */
	fun attachEventSource(tag: String, eventObservable: Observable<Event>) {
		if (eventSourcesMap.containsKey(tag).not()) {
			val newDisposable = eventObservable.subscribe(eventRelay)

			eventSourcesMap[tag] = newDisposable
			eventSourcesCompositeDisposable.add(newDisposable)
		}
	}

	fun attachEventSource(tag: String, newDisposable: Disposable) {
		if (eventSourcesMap.containsKey(tag).not()) {

			eventSourcesMap[tag] = newDisposable
			eventSourcesCompositeDisposable.add(newDisposable)
		}
	}

	private fun propagateStates(states: List<State>) {
		states.forEach { state ->
			val behaviorRelay: BehaviorRelay<State>? = stateRelayMap[state::class]
			behaviorRelay?.accept(state)
		}
	}

	private fun propagateSignals(signals: List<Signal>) {
		signals.forEach { signalRelay.accept(it) }
	}

  /**
   * Handles a list of side effects by iterating through them and attempting to find a suitable handler.
   *
   * For each side effect, the function iterates through a list of [sideEffectHandlers]. Each handler attempts to create a Flowable
   * that can handle the side effect. If a Flowable is successfully created:
   *  - It subscribes to the Flowable and forwards emissions to the [eventRelay].
   *  - It attaches a tag to the subscription for tracking purposes.
   *  - It marks the side effect as handled.
   *  - It breaks out of the handler loop.
   *
   * If no handler is found for a side effect, a [SideEffectNotHandledException] is thrown.
   *
   * @param sideEffects The list of side effects to handle.
   * @throws SideEffectNotHandledException if no handler is found for a side effect.
   */
	private fun handleSideEffects(sideEffects: List<SideEffect>) {
		sideEffects.forEach { sideEffect ->
			var handled = false
			for (handler in sideEffectHandlers) {
				val result = handler.createFlowable(sideEffect)
				val effectIsHandled = result.first
				val flowable = result.second
				if (flowable != null) {
					val disposable = flowable.subscribe(eventRelay)
					handler.setDisposable(disposable)
					val tag = "SideEffect:${sideEffect.javaClass.canonicalName}:${UUID.randomUUID()}"
					attachEventSource(tag, disposable)
				}
				if (effectIsHandled) {
					handled = true
					break
				}
			}

			if (handled.not()) throw SideEffectNotHandledException(sideEffect)
		}
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	fun getEventSources() = eventSourcesCompositeDisposable
}

interface OnConnectCallback {

	fun onConnect()
}

