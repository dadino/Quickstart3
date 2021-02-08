package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Start.Companion.start
import com.dadino.quickstart3.sample.entities.OnGoToSecondPageClicked
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal

class CounterViewModel : BaseViewModel<CounterState>() {

	override fun updater(): Updater<CounterState> {
		return CounterUpdater()
	}

	override fun getSideEffectHandlers() = listOf(
		AdvanceCounterSideEffectHandler(),
		DelayedAdvanceCounterSideEffectHandler()
	)
}

data class CounterState(
		val counter: Int = 0) : State()

class CounterUpdater : Updater<CounterState>(true) {

	override fun start(): Start<CounterState> {
		return start(CounterState(counter = 150))
	}

	override fun update(previous: CounterState, event: Event): Next<CounterState> {
		return when (event) {
			is CounterEvent.SetCounter -> justState(previous.copy(counter = event.newCounter))
			is CounterEvent.OnAdvanceCounterClicked -> justState(previous.copy(counter = previous.counter + 1))
			is CounterEvent.OnDelayedAdvanceCounterClicked -> justEffect(CounterEffect.DelayedAdvanceCounter(previous.counter, 1))
			is CounterEvent.OnShowCounterStateClicked -> justSignal(CounterSignal.ShowCounterState(previous.counter))
			is OnGoToSecondPageClicked -> justSignal(SpinnerSignal.OpenSecondActivity)
			else                                           -> noChanges()
		}
	}

	override fun getSubStateClasses(): List<Class<*>> {
		return listOf(CounterState::class.java)
	}
}