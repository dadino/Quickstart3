package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import timber.log.Timber

abstract class Flow<FLOW : Flow<FLOW, STATE, STEP>, STATE, STEP : FlowStep<STATE>>(protected val root: FlowStep<STATE>, protected val steps: List<STEP>) {
	constructor(root: STEP) : this(root = root, steps = listOf(root))

	fun hasRemainingSteps() = steps.isNotEmpty()
	fun getCurrentStep(): STEP? {
		return steps.lastOrNull()
	}

	fun onEvent(state: STATE, onEvent: Event): FlowAdvancement<STATE>? {
		val currentStep = getCurrentStep() ?: throw RuntimeException("Flow is empty")
		return currentStep.onEvent(state, onEvent)
	}

	fun applyAdvancement(advancement: FlowAdvancement<STATE>?): FLOW {
		return if (advancement != null) fromAdvancement(advancement) else getFlow()
	}

	private fun fromAdvancement(advancement: FlowAdvancement<STATE>): FLOW {
		val steps: List<STEP> = when (advancement) {
			is FlowAdvancement.ExitFlow               -> {
				Timber.d("----ExitFlow----")
				listOf()
			}
			is FlowAdvancement.GoToRoot               -> {
				val index = steps.indexOfFirst { root.key == it.key }
				val temp = arrayListOf<STEP>()
				temp.addAll(steps.subList(0, index + 1))
				Timber.d("<---GoToRoot----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoToStep<STATE, *>     -> {
				val temp = arrayListOf<STEP>()
				temp.addAll(steps)
				temp.add(advancement.step as STEP)
				Timber.d("----GoForward--->\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoBackToStep<STATE, *> -> {
				val index = steps.indexOfLast { advancement.step.key == it.key }
				val temp = arrayListOf<STEP>()
				temp.addAll(steps.subList(0, index + 1))
				Timber.d("<---GoBack----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoBackOneStep          -> {
				val temp = arrayListOf<STEP>()
				temp.addAll(steps.subList(0, steps.lastIndex))
				Timber.d("<---GoBack----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
		}

		return updateFlowWithSteps(steps)
	}

	abstract fun getFlow(): FLOW
	abstract fun updateFlowWithSteps(steps: List<STEP>): FLOW
}

