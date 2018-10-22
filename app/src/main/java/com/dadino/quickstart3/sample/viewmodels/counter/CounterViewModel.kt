package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.SideEffectHandler
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.Start.Companion.start
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.sample.entities.OnGoToSecondPageClicked
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal


class CounterViewModel : BaseViewModel<CounterState>() {
	init {
		connect()
	}

	override fun updater(): Updater<CounterState> {
		return CounterUpdater()
	}

	override fun getSideEffectHandlers() = listOf<SideEffectHandler>(AdvanceCounterSideEffectHandler())
}

data class CounterState(
		val counter: Int = 0) : State()

class CounterUpdater : Updater<CounterState>() {
	override fun start(): Start<CounterState> {
		return start(CounterState())
	}

	override fun update(previous: CounterState, event: Event): Next<CounterState> {
		return when (event) {
			is CounterEvent.SetCounter                -> justState(previous.copy(counter = event.newCounter))
			is CounterEvent.OnAdvanceCounterClicked   -> justEffect(CounterEffect.AdvanceCounter(previous.counter, 1))
			is CounterEvent.OnShowCounterStateClicked -> justSignal(CounterSignal.ShowCounterState(previous.counter))
			is OnGoToSecondPageClicked                -> justSignal(SpinnerSignal.OpenSecondActivity)
			else                                      -> noChanges()
		}
	}

}