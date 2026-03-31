package com.dadino.quickstart3.flow

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.utils.QuickLogger

abstract class Flow<FLOW : Flow<FLOW, STATE, STEP>, STATE, STEP : FlowStep<STATE>>(protected val root: STEP, protected val steps: List<STEP>) {
  constructor(root: STEP) : this(root = root, steps = listOf(root))

  fun hasRemainingSteps() = steps.isNotEmpty()
  fun getStep(stepId: String?): STEP? = steps.firstOrNull { it.key == stepId }
  fun getCurrentStep(): STEP? {
	return steps.lastOrNull()
  }

  fun getStepsToPublish(state: STATE): Map<String, Map<String, String>> {
	return steps.associate { it.key to it.getMetadata(state) }
  }

  abstract fun getFlow(): FLOW
  abstract fun updateFlowWithSteps(steps: List<STEP>): FLOW

  protected fun log(message: () -> String) = QuickLogger.tag(this::class.simpleName).d(message)
  override fun toString(): String {
	return "Flow(root= ${root.key}, steps= ${steps.joinToString(separator = ", ") { it.key }})"
  }
}

abstract class FlowWithStepGeneration<FLOW : Flow<FLOW, STATE, STEP>, STATE, STEP : FlowStepWithGeneration<STATE>>(root: STEP, steps: List<STEP>) : Flow<FLOW, STATE, STEP>(root, steps) {
  constructor(root: STEP) : this(root = root, steps = listOf(root))
}

abstract class FlowWithAdvancements<FLOW : Flow<FLOW, STATE, STEP>, STATE, STEP : FlowStepWithAdvancements<STATE>>(root: STEP, steps: List<STEP>) : Flow<FLOW, STATE, STEP>(root, steps) {
  constructor(root: STEP) : this(root = root, steps = listOf(root))

  fun advancementOnEvent(state: STATE, event: Event): Pair<STEP, FlowAdvancement<STATE>>? {
	log { "!--- advancementOnEvent: $event" }
	steps.reversed().forEach { step ->

	  val advancement = step.onEvent(state, event)
	  if (advancement != null) {
		log { "Step ${step.key} created $advancement with event $event" }
		return step to advancement
	  } else {
		log { "Step ${step.key} can't use event $event" }
	  }
	}
	log { "!--- can't use event to advance $event" }
	return null
  }

  fun applyAdvancement(startingStep: STEP, advancement: FlowAdvancement<STATE>): FLOW {
	return fromAdvancement(startingStep, advancement)
  }

  private fun fromAdvancement(startingStep: STEP?, advancement: FlowAdvancement<STATE>): FLOW {
	val steps: List<STEP> = when (advancement) {
	  is FlowAdvancement.ExitFlow               -> {
		log { "----ExitFlow----" }
		listOf()
	  }

	  is FlowAdvancement.GoToRoot               -> {
		val index = steps.indexOfFirst { root.key == it.key }
		val temp = arrayListOf<STEP>()
		temp.addAll(steps.subList(0, index + 1))
		log { "<---GoToRoot----\n${temp.joinToString("\n") { it.key }}" }
		temp
	  }

	  is FlowAdvancement.GoToStep<STATE, *>     -> {
		val temp = arrayListOf<STEP>()
		val startingStepIndex = steps.indexOfLast { it.key == startingStep?.key }

		temp.addAll(steps.subList(0, startingStepIndex + 1))
		advancement.steps.forEach {
		  temp.add(it as STEP)
		}
		log { "----GoForward--->\n${temp.joinToString("\n") { it.key }}" }
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
		  log { "<---GoBack----\n${temp.joinToString("\n") { it.key }}" }
		  temp
		} else {
		  log { "<---GoBack----\nCan't go back, because none of the GoBackToStep steps are in the current steps\n${steps.joinToString("\n") { it.key }}" }
		  steps
		}
	  }

	  is FlowAdvancement.GoBackOneStep          -> {
		val temp = arrayListOf<STEP>()
		val startingStepIndex = steps.indexOfLast { it.key == startingStep?.key }
		temp.addAll(steps.subList(0, startingStepIndex))
		log { "<---GoBack----\n${temp.joinToString("\n") { it.key }}" }
		temp
	  }
	}

	return updateFlowWithSteps(steps)
  }

  fun applyBackAdvancement(currentStep: STEP?, advancement: FlowAdvancement<STATE>): FLOW {
	log { "---- Preparing flow for back advancement: $advancement" }
	val steps: List<STEP> = when (advancement) {
	  is FlowAdvancement.ExitFlow               -> {
		listOfNotNull(currentStep)
	  }

	  is FlowAdvancement.GoToRoot               -> {
		val rootIndex = steps.indexOfFirst { root.key == it.key }
		val temp = arrayListOf<STEP>()
		temp.addAll(steps.subList(0, rootIndex + 1))
		if (currentStep != null) temp.add(currentStep)
		temp
	  }

	  is FlowAdvancement.GoToStep<STATE, *>     -> steps

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
		  if (currentStep != null) temp.add(currentStep)
		  temp
		} else {
		  steps
		}
	  }

	  is FlowAdvancement.GoBackOneStep          -> steps
	}
	log { "Updated flow steps:\n${steps.joinToString("\n") { it.key }}" }
	return updateFlowWithSteps(steps)
  }
}

