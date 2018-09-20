package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.SideEffectHandler
import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.next
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges


class CounterViewModel : BaseViewModel<CounterState>() {
	init {
		connect()
	}

	override fun updateFunction() = { previous: CounterState, event: Event ->
		when (event) {
			is CounterEvent.SetCounter                -> next(previous.copy(counter = event.newCounter))
			is CounterEvent.OnAdvanceCounterClicked   -> justEffect(CounterEffect.AdvanceCounter(previous.counter, 1))
			is CounterEvent.OnShowCounterStateClicked -> justSignal(CounterSignal.ShowCounterState(previous.counter))
			else                                      -> noChanges()
		}
	}

	override fun getStart() = Start.start(CounterState())

	override fun getSideEffectHandlers() = listOf<SideEffectHandler>(AdvanceCounterSideEffectHandler())
}

data class CounterState(
		val counter: Int = 0) : State()


class AdvanceCounterSideEffectHandler : SingleSideEffectHandler<CounterEffect.AdvanceCounter>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is CounterEffect.AdvanceCounter
	}

	override fun effectToEvent(effect: CounterEffect.AdvanceCounter): Event {
		return CounterEvent.SetCounter(effect.currentCounter + effect.amount)
	}
}

sealed class CounterEffect : SideEffect() {
	class AdvanceCounter(val currentCounter: Int, val amount: Int) : SideEffect()
}

sealed class CounterSignal : Signal() {
	class ShowCounterState(val counter: Int) : Signal()
}

sealed class CounterEvent : Event() {
	class SetCounter(val newCounter: Int) : Event()
	object OnAdvanceCounterClicked : Event()
	object OnShowCounterStateClicked : Event()
}