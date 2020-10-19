package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.core.components.RxSingleSideEffectHandler
import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class AdvanceCounterSideEffectHandler : SingleSideEffectHandler<CounterEffect.AdvanceCounter>() {

	override fun checkClass(effect: SideEffect): Boolean {
		return effect is CounterEffect.AdvanceCounter
	}

	override fun effectToEvent(effect: CounterEffect.AdvanceCounter): Event {
		return CounterEvent.SetCounter(effect.currentCounter + effect.amount)
	}
}

class DelayedAdvanceCounterSideEffectHandler
	: RxSingleSideEffectHandler<CounterEffect.DelayedAdvanceCounter>(disposeOnNewEffect = true) {

	override fun checkClass(effect: SideEffect): Boolean {
		return effect is CounterEffect.DelayedAdvanceCounter
	}

	override fun effectToFlowable(effect: CounterEffect.DelayedAdvanceCounter): Flowable<Event> {
		return Flowable.just(effect)
			.delay(400, TimeUnit.MILLISECONDS)
			.map<Event> { CounterEvent.SetCounter(effect.currentCounter + effect.amount) }
	}
}
