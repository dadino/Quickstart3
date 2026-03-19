package com.dadino.quickstart3.core.entities

import androidx.compose.runtime.Stable

/**
 * Represents the state of a component or system.  States are often used in state machines
 * or reactive programming to manage transitions and behavior changes.
 */
@Stable
interface State {

  fun getStatesToPropagate(isInitialization: Boolean, previousState: State): List<State> {
		if (isInitialization || previousState != this) return listOf(this)
		return listOf()
	}
}
