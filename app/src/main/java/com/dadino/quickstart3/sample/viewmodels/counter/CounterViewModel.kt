package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.Updater
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

	override fun updater(): Updater<CounterState> {
		return CounterUpdater()
	}

	override fun getSideEffectHandlers() = listOf(
		AdvanceCounterSideEffectHandler(),
		DelayedAdvanceCounterSideEffectHandler()
	)

	override fun wantOnCreateEvent() = true
	override fun wantOnStartEvent() = true
	override fun wantOnResumeEvent() = true
	override fun wantOnPauseEvent() = true
	override fun wantOnStopEvent() = true
	override fun wantOnDestroyEvent() = true
}

data class CounterState(
	val counter: Int = 0
) : State {

	override fun getStatesToPropagate(isInitialization: Boolean, previousState: State): List<State> {
		check(previousState is CounterState)
		val list = arrayListOf<State>()
		if (previousState.counter > 155 != counter > 155 || isInitialization) list.add(CounterSubState(isGreatEnough = counter > 155))
		list.addAll(super.getStatesToPropagate(isInitialization, previousState))
		return list
	}
}

data class CounterSubState(val isGreatEnough: Boolean) : State

class CounterUpdater : Updater<CounterState> {

	override fun start(): Start<CounterState> {
		return start(getInitialMainState())
	}

	override fun update(previous: CounterState, event: Event): Next<CounterState> {
		return when (event) {
			is CounterEvent.SetCounter                     -> justState(previous.copy(counter = event.newCounter))
			is CounterEvent.OnAdvanceCounterClicked        -> justState(previous.copy(counter = previous.counter + 1))
			is CounterEvent.OnDelayedAdvanceCounterClicked -> justEffect(CounterEffect.DelayedAdvanceCounter(previous.counter, 1))
			is CounterEvent.OnShowCounterStateClicked      -> justSignal(CounterSignal.ShowCounterState(previous.counter))
			is OnGoToSecondPageClicked                     -> justSignal(SpinnerSignal.OpenSecondActivity)
			else                                           -> noChanges()
		}
	}

	override fun getInitialMainState(): CounterState {
		return CounterState(counter = 150)
	}

	override fun getInitialSubStates(): List<State> {
		return listOf(
			CounterSubState(false)
		)
	}
}