package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.InitializeState
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State
import timber.log.Timber

abstract class Updater<STATE : State>(var enableLogging: Boolean = false) {


	abstract fun start(): Start<STATE>
	abstract fun update(previous: STATE, event: Event): Next<STATE>
	fun getStatesToPropagate(previous: STATE, updated: STATE, isInitialization: Boolean): List<State> {
		return updated.getStatesToPropagate(isInitialization, previous)
	}

	fun internalUpdate(previous: STATE, event: Event): Next<STATE> {
	  if (enableLogging) Timber.d("________________${previous.javaClass.simpleName}______________________")
		if (enableLogging) Timber.d("IN: ${event.javaClass.simpleName}")
		val next = if (event is InitializeState) {
			start()
		} else {
			update(previous, event)
		}
		if (enableLogging) Timber.d("OUT: $next")
	  if (enableLogging) Timber.d("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯${previous.javaClass.simpleName}¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯")
		return next
	}

	abstract fun getInitialMainState(): STATE
	abstract fun getInitialSubStates(): List<State>
}