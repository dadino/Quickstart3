package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.ILogger
import com.dadino.quickstart3.core.utils.LogcatLogger

abstract class Updater<STATE : State>(var enableLogging: Boolean = false) {

	var logger: ILogger = LogcatLogger()

	abstract fun start(): Start<STATE>
	abstract fun update(previous: STATE, event: Event): Next<STATE>
	open fun updateSubStates(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
		return if (previous != updated)
			listOf(updated)
		else listOf()
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

	abstract fun getSubStateClasses(): List<Class<*>>
}