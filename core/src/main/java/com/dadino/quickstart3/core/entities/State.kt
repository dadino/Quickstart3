package com.dadino.quickstart3.core.entities

abstract class State {

	open fun getStatesToPropagate(isInitialization: Boolean, previousState: State): List<State> {
		if (isInitialization || previousState != this) return listOf(this)
		return listOf()
	}
}
