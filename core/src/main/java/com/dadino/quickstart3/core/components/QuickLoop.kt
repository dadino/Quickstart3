package com.dadino.quickstart3.core.components

import androidx.annotation.VisibleForTesting
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.*
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlin.reflect.KClass

class QuickLoop<STATE : State>(private val loopName: String,
							   private val updater: Updater<STATE>,
							   private val sideEffectHandlers: List<SideEffectHandler> = listOf(),
							   private val onConnectCallback: OnConnectCallback

) {

	var logger: ILogger = LogcatLogger()
	var enableLogging = false
	var canReceiveEvents = false

	private val eventSourcesCompositeDisposable = CompositeDisposable()

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
	private val internalDisposable: Disposable = eventRelay.filter { it !is NoOpEvent }
		.toFlowable(BackpressureStrategy.BUFFER)
		.startWith(InitializeState)
		.map { event ->
			val previousState = currentState()
			val next = updater.internalUpdate(previousState, event)
			val newState = next.state ?: previousState
			val isInitialization = next is Start<STATE>
			val updatedSubStates = updater.getStatesToPropagate(previousState, newState, isInitialization)
			InternalNext(states = updatedSubStates, signals = next.signals, effects = next.effects, isStartingState = isInitialization)
		}
		.toAsync()
		.subscribeBy(onNext = { next ->
			onNext(next)
		})

	fun disconnect() {
		internalDisposable.dispose()
		eventSourcesCompositeDisposable.clear()
		sideEffectHandlers.forEach { it.onClear() }
	}

	fun getStateFlows(): List<Flowable<out State>> = stateFlowableMap.entries.map { it.value }
	fun getStateFlow(subStateClass: KClass<out State>): Flowable<out State> =
		stateFlowableMap[subStateClass] ?: throw RuntimeException("No states for class ${subStateClass.qualifiedName}")

	fun getSubStates(): List<State> = stateRelayMap.entries.mapNotNull { it.value.value }
	fun getSubState(subStateClass: KClass<out State>): State? {
		return stateRelayMap[subStateClass]?.value
	}

	fun currentState(): STATE {
		return mainStateRelay.value!!
	}

	fun receiveEvent(event: Event) {
		eventRelay.accept(event)
	}

	fun attachEventSource(eventObservable: Observable<Event>) {
		eventSourcesCompositeDisposable.add(eventObservable.subscribe(eventRelay))
	}

	fun attachEventSource(eventDisposable: Disposable) {
		eventSourcesCompositeDisposable.add(eventDisposable)
	}

	private fun onNext(next: InternalNext) {
		if (next.states.isNotEmpty()) {
			propagateStates(next.states)
		}
		if (next.signals.isNotEmpty()) {
			propagateSignals(next.signals)
		}
		if (next.effects.isNotEmpty()) {
			handleSideEffects(next.effects)
		}

		if (next.isStartingState) {
			canReceiveEvents = true
			onConnectCallback.onConnect()
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

	private fun handleSideEffects(sideEffects: List<SideEffect>) {
		sideEffects.forEach { sideEffect ->
			var handled = false
			for (handler in sideEffectHandlers) {
				val flowable = handler.createFlowable(sideEffect)
				if (flowable != null) {
					val disposable = flowable.subscribe(eventRelay)
					handler.setDisposable(disposable)
					attachEventSource(disposable)
					handled = true
					break
				}
			}

			if (handled.not()) throw SideEffectNotHandledException(sideEffect)
		}
	}

	private fun log(createMessage: () -> String) {
		if (enableLogging) logger.log(loopName, createMessage())
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	fun getEventSources() = eventSourcesCompositeDisposable
}

interface OnConnectCallback {

	fun onConnect()
}

