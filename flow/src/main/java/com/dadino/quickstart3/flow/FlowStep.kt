package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event

interface FlowStep<STATE> {
  val key: String

  open fun getMetadata(state: STATE?): Map<String, String> = emptyMap()
}

abstract class FlowStepWithAdvancements<STATE>(override val key: String) : FlowStep<STATE> {

  open fun getAdvancementForBack(state: STATE): FlowAdvancement<STATE>? = null

  abstract fun onEvent(state: STATE, event: Event): FlowAdvancement<STATE>?

  override fun toString(): String {
	return key
  }
}

abstract class FlowStepWithGeneration<STATE>(override val key: String) : FlowStep<STATE> {

  override fun toString(): String {
	return key
  }
}