package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import timber.log.Timber

data class Flow<STATE>(private val root: FlowStep<STATE>, private val steps: List<FlowStep<STATE>>) {
	constructor(root: FlowStep<STATE>) : this(root = root, steps = listOf(root))

	fun hasRemainingSteps() = steps.isNotEmpty()
	fun getCurrentStep(): FlowStep<STATE>? {
		return steps.lastOrNull()
	}

	fun onEvent(state: STATE, onEvent: Event): FlowAdvancement<STATE>? {
		val currentStep = getCurrentStep() ?: throw RuntimeException("Flow is empty")
		return currentStep.onEvent(state, onEvent)
	}

	fun applyAdvancement(advancement: FlowAdvancement<STATE>?): Flow<STATE> {
		return if (advancement != null) fromAdvancement(advancement) else this
	}

	private fun fromAdvancement(advancement: FlowAdvancement<STATE>): Flow<STATE> {
		val steps: List<FlowStep<STATE>> = when (advancement) {
			is FlowAdvancement.ExitFlow      -> {
				Timber.d("----ExitFlow----")
				listOf()
			}
			is FlowAdvancement.GoToRoot      -> {
				val index = steps.indexOfFirst { root.key == it.key }
				val temp = arrayListOf<FlowStep<STATE>>()
				temp.addAll(steps.subList(0, index + 1))
				Timber.d("<---GoToRoot----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoToStep      -> {
				val temp = arrayListOf<FlowStep<STATE>>()
				temp.addAll(steps)
				temp.add(advancement.step)
				Timber.d("----GoForward--->\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoBackToStep  -> {
				val index = steps.indexOfLast { advancement.step.key == it.key }
				val temp = arrayListOf<FlowStep<STATE>>()
				temp.addAll(steps.subList(0, index + 1))
				Timber.d("<---GoBack----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
			is FlowAdvancement.GoBackOneStep -> {
				val temp = arrayListOf<FlowStep<STATE>>()
				temp.addAll(steps.subList(0, steps.lastIndex))
				Timber.d("<---GoBack----\n${temp.joinToString("\n") { it.key }}")
				temp
			}
		}

		return this.copy(steps = steps)
	}
}

