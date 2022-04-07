package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.Signal

sealed class FlowAdvancement<STATE>(val advancementType: AdvancementType) {
	class GoToRoot<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
	class ExitFlow<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
	class GoToStep<STATE, STEP : FlowStep<STATE>>(val steps: List<STEP>) : FlowAdvancement<STATE>(AdvancementType.Enter) {
		constructor(step: STEP) : this(listOf(step))
	}

	class GoBackToStep<STATE, STEP : FlowStep<STATE>>(val steps: List<STEP>) : FlowAdvancement<STATE>(AdvancementType.Exit) {
		constructor(step: STEP) : this(listOf(step))
	}

	class GoBackOneStep<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
}

sealed class AdvancementType {
	object Enter : AdvancementType()
	object Exit : AdvancementType()
}

object CloseFlow : Signal()