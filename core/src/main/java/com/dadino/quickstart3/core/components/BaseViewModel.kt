package com.dadino.quickstart3.core.components

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.toAsync
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

abstract class BaseViewModel<STATE : Any> : ViewModel() {
	private lateinit var state: STATE

	private val userActionRelay: PublishRelay<Event> by lazy { PublishRelay.create<Event>() }

	private val nexts: Flowable<Next<STATE>>  by lazy {
		userActionRelay.doOnNext { Log.d(className(), "<---- ${it.javaClass.simpleName}") }
				.toFlowable(BackpressureStrategy.BUFFER)
				.startWith(InitializeState)
				.map { event ->
					Log.d(className(), "Updating with event: ${event.javaClass.simpleName}")
					update(state, event)
				}
				.doOnNext { next ->
					if (next.state != null) {
						state = next.state
						publishNewState(state)
					}
					if (next.signals.isNotEmpty()) {
						publishNewSignals(next.signals)
					}
					if (next.effects.isNotEmpty()) {
						publishNewSideEffects(next.effects)
					}
				}
				.doOnNext { Log.d(className(), "Next: $it") }
				.toAsync()
				.distinctUntilChanged()
				.replay(1)
				.autoConnect(0)
	}

	private fun publishNewState(state: STATE) {
		//TODO
	}

	private fun publishNewSignals(signals: List<Signal>) {
		//TODO
	}

	private fun publishNewSideEffects(sideEffect: List<SideEffect>) {
		//TODO
	}

	val states: Flowable<STATE>  by lazy {
		nexts.filter { it.state != null }
				.map { it.state!! }
				.distinctUntilChanged()
				.onBackpressureLatest()
	}

	val signals: Flowable<List<Signal>>  by lazy {
		nexts.filter { it.signals.isNotEmpty() }
				.map { it.signals }
	}

	fun dispatchEvent(action: Event) {
		userActionRelay.accept(action)
	}


	fun currentState(): STATE {
		return state
	}

	protected abstract fun update(previous: STATE, event: Event): Next<STATE>

	protected abstract fun initialState(): Next<STATE>

	private fun className(): String {
		return javaClass.simpleName
	}

	override fun onCleared() {
		super.onCleared()
		//todo dispose of effect handlers
	}
}