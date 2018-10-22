package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.*


abstract class Updater<STATE : State>(var enableLogging: Boolean = false) {
	abstract fun start(): Start<STATE>
	abstract fun update(previous: STATE, event: Event): Next<STATE>

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
		if (enableLogging) Log.d(javaClass.simpleName, createMessage())
	}
}