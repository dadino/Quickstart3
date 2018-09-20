package com.dadino.quickstart3.core.components

import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State

abstract class BaseViewModel<STATE : State> : ViewModel() {
	private val loop: QuickLoop<STATE> by lazy {
		QuickLoop(loopName = javaClass.simpleName,
				sideEffectHandlers = getSideEffectHandlers(),
				start = getStart(),
				update = updateFunction())
	}

	protected fun connect() {
		loop.connect()
	}

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

	abstract fun updateFunction(): (STATE, Event) -> Next<STATE>

	abstract fun getStart(): Start<STATE>

	abstract fun getSideEffectHandlers(): List<SideEffectHandler>
}