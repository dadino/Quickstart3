package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.State
import kotlin.reflect.KClass

abstract class MoveFlowState<STATE>(
	open val flow: Flow<STATE>
) : State() {

	protected abstract fun getState(): STATE
	abstract fun updateWithFlow(flow: Flow<STATE>): STATE
	open fun getEventsToSkip(): List<KClass<*>> {
		return listOf()
	}
}
