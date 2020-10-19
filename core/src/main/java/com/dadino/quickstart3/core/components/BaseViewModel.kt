package com.dadino.quickstart3.core.components

import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.core.entities.*
import io.reactivex.Observable

abstract class BaseViewModel<STATE : State> : ViewModel() {

	var actionsToPerformOnConnect: List<() -> Unit> = listOf()
	private val loop: QuickLoop<STATE> by lazy {
		QuickLoop(
			loopName = javaClass.simpleName,
			sideEffectHandlers = getSideEffectHandlers(),
			updater = updater(),
			actionToPerformOnConnect = actionsToPerformOnConnect
		)
	}

	protected fun enableLogging(enableLogging: Boolean) {
		loop.enableLogging = enableLogging
	}

	override fun onCleared() {
		super.onCleared()
		loop.disconnect()
	}

	fun receiveEvent(event: Event) {
		loop.receiveEvent(event)
	}

	fun attachEventSource(eventObservable: Observable<Event>) {
		loop.attachEventSource(eventObservable)
	}

	fun waitForSideEffect(sideEffect: SideEffect, handler: SideEffectHandler, doOnComplete: (error: Throwable?) -> Unit) {
		loop.waitForSideEffect(sideEffect, handler, doOnComplete)
	}

	fun currentState() = loop.currentState()
	fun states() = loop.states
	fun signals() = loop.signals

	abstract fun updater(): Updater<STATE>

	abstract fun getSideEffectHandlers(): List<SideEffectHandler>

	fun canReceiveEvents() = loop.canReceiveEvents
}