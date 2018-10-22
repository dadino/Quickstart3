package com.dadino.quickstart3.core.components

import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.State

abstract class BaseViewModel<STATE : State> : ViewModel(), QuickLoop.ConnectionCallbacks {

	private val loop: QuickLoop<STATE> by lazy {
		QuickLoop(
				loopName = javaClass.simpleName,
				sideEffectHandlers = getSideEffectHandlers(),
				updater = updater()
		).apply { connectionCallbacks = this@BaseViewModel }
	}

	protected fun enableLogging(enableLogging: Boolean) {
		loop.enableLogging = enableLogging
	}

	protected fun connect() {
		loop.connect()
	}

	override fun onLoopConnected() {}

	override fun onLoopDisconnected() {}

	override fun onCleared() {
		super.onCleared()
		loop.disconnect()
	}

	fun receiveEvent(event: Event) {
		loop.receiveEvent(event)
	}

	fun currentState() = loop.currentState()
	fun states() = loop.states
	fun signals() = loop.signals

	abstract fun updater(): Updater<STATE>

	abstract fun getSideEffectHandlers(): List<SideEffectHandler>
}