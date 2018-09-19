package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.sample.entities.AddToCounter
import com.dadino.quickstart3.sample.entities.OnAdvanceCounterClicked


class CounterViewModel : BaseViewModel<CounterState>() {
	override fun reducer(): Updater<CounterState> {
		return CounterUpdater()
	}

	override fun reactToUserAction(currentState: CounterState, action: UserAction) {
		when (action) {
			is OnAdvanceCounterClicked -> pushCommand(AddToCounter())
		}
	}

	override fun initialState(): CounterState {
		return CounterState()
	}
}

data class CounterState(
		val counter: Int = 0)

class CounterUpdater : Updater<CounterState> {
	override fun reduce(previous: CounterState, command: StateCommand): CounterState {
		return CounterState(
				counter = when (command) {
					is AddToCounter -> previous.counter + 1
					else            -> previous.counter
				}
		)
	}
}