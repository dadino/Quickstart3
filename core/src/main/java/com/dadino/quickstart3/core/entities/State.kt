package com.dadino.quickstart3.core.entities

open class State {

	open fun shouldPropagateUpdate(previousState: State): Boolean {
		return previousState != this
	}
}