package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.toAsync
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy


class QuickLoop<STATE : State>(private val loopName: String,
							   private val updater: Updater<STATE>,
							   private val sideEffectHandlers: List<SideEffectHandler> = arrayListOf()
) {
	private lateinit var state: STATE

	private val eventSourcesCompositeDisposable = CompositeDisposable()

	private val eventRelay: PublishRelay<Event> by lazy { PublishRelay.create<Event>() }
	private val stateRelay: PublishRelay<STATE> by lazy { PublishRelay.create<STATE>() }
	private val signalRelay: PublishRelay<Signal> by lazy { PublishRelay.create<Signal>() }

	val states: Flowable<STATE>  by lazy {
		stateRelay.toFlowable(BackpressureStrategy.LATEST)
				.distinctUntilChanged()
				.replay(1)
				.autoConnect(0)
	}

	val signals: Flowable<Signal>  by lazy {
		signalRelay.toFlowable(BackpressureStrategy.BUFFER)
	}

	var connectionCallbacks: ConnectionCallbacks? = null
	var isConnected: Boolean = false
		private set
	private val actionToPerformOnConnect = arrayListOf<() -> Unit>()
	var enableLogging = false

	fun connect() {
		state = updater.start().startState

		sideEffectHandlers.forEach { it.connectTo(eventRelay) }

		eventRelay.filter { it !is NoOpEvent }
				.toFlowable(BackpressureStrategy.BUFFER)
				.startWith(InitializeState)
				.map { event -> updater.internalUpdate(state, event) }
				.toAsync()
				.subscribeBy(onNext = { next ->
					onNext(next)
				})


	}

	fun disconnect() {
		sideEffectHandlers.forEach { it.dispose() }

		isConnected = false
		eventSourcesCompositeDisposable.clear()
		connectionCallbacks?.onLoopDisconnected()
	}

	fun doOnConnect(action: () -> Unit) {
		actionToPerformOnConnect.add(action)
	}

	fun currentState(): STATE {
		return state
	}

	fun receiveEvent(event: Event) {
		if (isConnected) eventRelay.accept(event)
		else doOnConnect { eventRelay.accept(event) }
	}

	fun attachEventSource(eventObservable: Observable<Event>) {
		eventSourcesCompositeDisposable.add(eventObservable.subscribe(eventRelay))
	}

	private fun onNext(next: Next<STATE>) {
		if (next.state != null) {
			state = next.state
			propagateState(state)
		}
		if (next.signals.isNotEmpty()) {
			propagateSignals(next.signals)
		}
		if (next.effects.isNotEmpty()) {
			handleSideEffects(next.effects)
		}

		if (next is Start<STATE>) {
			isConnected = true

			connectionCallbacks?.onLoopConnected()

			actionToPerformOnConnect.forEach { it() }
			actionToPerformOnConnect.clear()
		}
	}

	private fun propagateState(state: STATE) {
		stateRelay.accept(state)
	}

	private fun propagateSignals(signals: List<Signal>) {
		signals.forEach { signalRelay.accept(it) }
	}

	private fun handleSideEffects(sideEffects: List<SideEffect>) {
		sideEffects.forEach { sideEffect ->
			var handled = false
			for (handler in sideEffectHandlers) {
				handled = handler.handle(sideEffect)
				if (handled) break
			}
			if (handled.not()) throw SideEffectNotHandledException(sideEffect)
		}
	}

	private fun log(createMessage: () -> String) {
		if (enableLogging) Log.d(loopName, createMessage())
	}

	interface ConnectionCallbacks {
		fun onLoopConnected()
		fun onLoopDisconnected()
	}
}

