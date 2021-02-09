package com.dadino.quickstart3.core.entities

abstract class State<STATE : State<STATE>> {

	open fun getStatesToPropagate(isInitialization: Boolean, previousState: STATE): List<State<*>> {
		if (isInitialization || previousState != this) return listOf(this)
		return listOf()
	}
}
