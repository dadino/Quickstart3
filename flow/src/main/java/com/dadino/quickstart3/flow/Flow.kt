package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.utils.QuickLogger

abstract class Flow<FLOW : Flow<FLOW, STATE, STEP>, STATE, STEP : FlowStep<STATE>>(protected val root: FlowStep<STATE>, protected val steps: List<STEP>) {
  constructor(root: STEP) : this(root = root, steps = listOf(root))

  fun hasRemainingSteps() = steps.isNotEmpty()
  fun getCurrentStep(): STEP? {
	return steps.lastOrNull()
  }

  fun onEvent(state: STATE, onEvent: Event): FlowAdvancement<STATE>? {
	val currentStep = getCurrentStep() ?: return FlowAdvancement.ExitFlow()
	return currentStep.onEvent(state, onEvent)
  }

  fun applyAdvancement(advancement: FlowAdvancement<STATE>?): FLOW {
	return if (advancement != null) fromAdvancement(advancement) else getFlow()
  }

  private fun fromAdvancement(advancement: FlowAdvancement<STATE>): FLOW {
	val steps: List<STEP> = when (advancement) {
	  is FlowAdvancement.ExitFlow               -> {
		QuickLogger.tag(tag()).d { "----ExitFlow----" }
		listOf()
	  }

	  is FlowAdvancement.GoToRoot               -> {
		val index = steps.indexOfFirst { root.key == it.key }
		val temp = arrayListOf<STEP>()
		temp.addAll(steps.subList(0, index + 1))
		QuickLogger.tag(tag()).d { "<---GoToRoot----\n${temp.joinToString("\n") { it.key }}" }
		temp
	  }

	  is FlowAdvancement.GoToStep<STATE, *>     -> {
		val temp = arrayListOf<STEP>()
		temp.addAll(steps)
		advancement.steps.forEach {
		  temp.add(it as STEP)
		}
		QuickLogger.tag(tag()).d { "----GoForward--->\n${temp.joinToString("\n") { it.key }}" }
		temp
	  }

	  is FlowAdvancement.GoBackToStep<STATE, *> -> {
		val index = when {
		  advancement.steps.isEmpty() -> null
		  advancement.steps.size == 1 -> {
			val i = steps.indexOfLast { advancement.steps[0].key == it.key }
			if (i >= 0) i else null
		  }

		  else                        -> advancement.steps.firstOrNull { targetStep -> steps.indexOfLast { stepInFlow -> targetStep.key == stepInFlow.key } >= 0 }
			?.let { targetStep -> steps.indexOfLast { targetStep.key == it.key } }
		}

		if (index != null) {
		  val temp = arrayListOf<STEP>()
		  temp.addAll(steps.subList(0, index + 1))
		  QuickLogger.tag(tag()).d { "<---GoBack----\n${temp.joinToString("\n") { it.key }}" }
		  temp
		} else {
		  QuickLogger.tag(tag()).d { "<---GoBack----\nCan't go back, because none of the GoBackToStep steps are in the current steps\n${steps.joinToString("\n") { it.key }}" }
		  steps
		}
	  }

	  is FlowAdvancement.GoBackOneStep          -> {
		val temp = arrayListOf<STEP>()
		temp.addAll(steps.subList(0, steps.lastIndex))
		QuickLogger.tag(tag()).d { "<---GoBack----\n${temp.joinToString("\n") { it.key }}" }
		temp
	  }
	}

	return updateFlowWithSteps(steps)
  }

  abstract fun getFlow(): FLOW
  abstract fun updateFlowWithSteps(steps: List<STEP>): FLOW

  private fun tag() = this::class.simpleName

  override fun toString(): String {
	return "Flow(root= ${root.key}, steps= ${steps.joinToString(", ") { it.key }})"
  }
}

