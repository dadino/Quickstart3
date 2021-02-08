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

class QuickLoop<STATE : State>(private val loopName: String,
							   private val updater: Updater<STATE>,
							   private val sideEffectHandlers: List<SideEffectHandler> = listOf(),
							   private val onConnectCallback: OnConnectCallback

) {

	var logger: ILogger = LogcatLogger()
	var enableLogging = false
	var canReceiveEvents = false

	private var state: STATE = updater.start().startState
	private val eventSourcesCompositeDisposable = CompositeDisposable()

	private val stateRelayMap: Map<String, BehaviorRelay<State>> by lazy {
		val relays = hashMapOf<String, BehaviorRelay<State>>()
		updater.getSubStateClasses().forEach { c -> relays[c.name] = BehaviorRelay.create<State>() }
		relays
	}
	private val stateFlowableMap: Map<String, Flowable<out State>> by lazy {
		val flowables = hashMapOf<String, Flowable<out State>>()
		stateRelayMap.entries.forEach {
			flowables[it.key] = it.value
				.toFlowable(BackpressureStrategy.LATEST)
				.replay(1)
				.autoConnect(0)
		}
		flowables
	}

	fun getStates(): List<Flowable<out State>> = stateFlowableMap.entries.map { it.value }
	fun getStateFlow(subStateClass: Class<*>): Flowable<out State> =
		stateFlowableMap[subStateClass.name] ?: throw RuntimeException("No states for class ${subStateClass.name}")

	fun getSubState(subStateClass: Class<*>): State? {
		return stateRelayMap[subStateClass.name]?.value
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
			val next = updater.internalUpdate(state, event)
			val previousState = state
			state = next.state ?: previousState
			val isInitialization = next is Start<STATE>
			val updatedSubStates = updater.updateSubStates(previousState, state, isInitialization)
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

	fun currentState(): STATE {
		return state
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

	fun waitForSideEffect(sideEffect: SideEffect, handler: SideEffectHandler, doOnCompleted: (error: Throwable?) -> Unit) {
		val em = PublishRelay.create<Event>()
		val wait = em.subscribeBy(
			onNext = {
				when (it) {
					is OnCompleteEvent -> doOnCompleted(null)
				}
			},
			onError = { doOnCompleted(it) },
			onComplete = { doOnCompleted(null) })
		handler.createObservable(em, sideEffect)
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
			val behaviorRelay: BehaviorRelay<State>? = stateRelayMap[state.javaClass.name]
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
				val disposable = handler.createObservable(eventRelay, sideEffect)
				if (disposable != null) {
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

