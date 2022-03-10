package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.Signal

sealed class FlowAdvancement<STATE>(val advancementType: AdvancementType) {
	class GoToRoot<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
	class ExitFlow<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
	class GoToStep<STATE, STEP : FlowStep<STATE>>(val step: STEP) : FlowAdvancement<STATE>(AdvancementType.Enter)
	class GoBackToStep<STATE, STEP : FlowStep<STATE>>(val step: STEP) : FlowAdvancement<STATE>(AdvancementType.Exit)
	class GoBackOneStep<STATE> : FlowAdvancement<STATE>(AdvancementType.Exit)
}

sealed class AdvancementType {
	object Enter : AdvancementType()
	object Exit : AdvancementType()
}

object CloseFlow : Signal()