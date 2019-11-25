package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.toAsync
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy


class QuickLoop<STATE : State>(private val loopName: String,
							   private val updater: Updater<STATE>,
							   private val sideEffectHandlers: List<SideEffectHandler> = arrayListOf()
) {
	private var state: STATE = updater.start().startState

	private val eventSourcesCompositeDisposable = CompositeDisposable()

	private val eventRelay: PublishRelay<Event> = PublishRelay.create<Event>()
	private val internalDisposable: Disposable = eventRelay.filter { it !is NoOpEvent }
			.toFlowable(BackpressureStrategy.BUFFER)
			.startWith(InitializeState)
			.map { event -> updater.internalUpdate(state, event) }
			.map { next ->
				if (next.state != null) {
					state = next.state
				}
				next
			}
			.toAsync()
			.subscribeBy(onNext = { next ->
				onNext(next)
			})

	private val stateRelay: PublishRelay<STATE> by lazy { PublishRelay.create<STATE>() }
	val states: Flowable<STATE>  by lazy {
		stateRelay.toFlowable(BackpressureStrategy.LATEST)
				.distinctUntilChanged()
				.replay(1)
				.autoConnect(0)
	}

	private val signalRelay: PublishRelay<Signal> by lazy { PublishRelay.create<Signal>() }
	val signals: Flowable<Signal>  by lazy {
		signalRelay.toFlowable(BackpressureStrategy.BUFFER)
	}

	private val actionToPerformOnConnect = arrayListOf<() -> Unit>()
	var enableLogging = false

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

	private fun onNext(next: Next<STATE>) {
		if (next.state != null) {
			propagateState(state)
		}
		if (next.signals.isNotEmpty()) {
			propagateSignals(next.signals)
		}
		if (next.effects.isNotEmpty()) {
			handleSideEffects(next.effects)
		}

		if (next is Start<STATE>) {
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
		if (enableLogging) Log.d(loopName, createMessage())
	}
}

