package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.State
import kotlin.reflect.KClass

abstract class FlowState<STATE, STEP : FlowStep<STATE>>(
	open val flow: Flow<STATE, STEP>
) : State() {

	protected abstract fun getState(): STATE
	abstract fun updateWithFlow(flow: Flow<STATE, STEP>): STATE
	open fun getEventsToSkip(): List<KClass<*>> {
		return listOf()
	}
}
