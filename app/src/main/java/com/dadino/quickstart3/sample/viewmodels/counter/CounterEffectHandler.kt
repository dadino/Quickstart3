package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect


class AdvanceCounterSideEffectHandler : SingleSideEffectHandler<CounterEffect.AdvanceCounter>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is CounterEffect.AdvanceCounter
	}

	override fun effectToEvent(effect: CounterEffect.AdvanceCounter): Event {
		return CounterEvent.SetCounter(effect.currentCounter + effect.amount)
	}
}
