package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event

abstract class FlowStep<STATE>(val key: String) {

	abstract fun onEvent(state: STATE, event: Event): FlowAdvancement<STATE>?

	override fun toString(): String {
		return key
	}
}