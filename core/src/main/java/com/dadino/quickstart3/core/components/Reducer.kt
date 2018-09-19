package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.StateCommand


interface Reducer<STATE> {
	fun reduce(previous: STATE, command: StateCommand): STATE
}