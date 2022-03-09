package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.InitializeState
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.ILogger
import com.dadino.quickstart3.core.utils.LogcatLogger

abstract class Updater<STATE : State>(var enableLogging: Boolean = false) {

	var logger: ILogger = LogcatLogger()

	abstract fun start(): Start<STATE>
	abstract fun update(previous: STATE, event: Event): Next<STATE>
	fun getStatesToPropagate(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
		return updated.getStatesToPropagate(isInitialization, previous)
	}

	fun internalUpdate(previous: STATE, event: Event): Next<STATE> {
		log { "______________________________________________" }
		log { "IN: ${event.javaClass.simpleName}" }
		val next = if (event is InitializeState) {
			start()
		} else {
			update(previous, event)
		}
		log { "OUT: $next" }
		log { "¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯" }
		return next
	}

	private fun log(createMessage: () -> String) {
		if (enableLogging) logger.log(javaClass.simpleName, createMessage())
	}

	abstract fun getInitialMainState(): STATE
	abstract fun getInitialSubStates(): List<State>
}