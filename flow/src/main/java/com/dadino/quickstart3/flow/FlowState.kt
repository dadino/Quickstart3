package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.State
import kotlin.reflect.KClass

abstract class FlowState<FLOW : Flow<STATE, STEP>, STATE, STEP : FlowStep<STATE>>(
	open val flow: FLOW
) : State() {

	protected abstract fun getState(): STATE
	abstract fun updateWithFlow(flow: FLOW): STATE
	open fun getEventsToSkip(): List<KClass<*>> {
		return listOf()
	}
}
