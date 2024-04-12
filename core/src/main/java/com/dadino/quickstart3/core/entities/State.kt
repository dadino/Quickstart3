package com.dadino.quickstart3.core.entities

interface State {

  fun getStatesToPropagate(isInitialization: Boolean, previousState: State): List<State> {
		if (isInitialization || previousState != this) return listOf(this)
		return listOf()
	}
}
