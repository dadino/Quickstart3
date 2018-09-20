package com.dadino.quickstart3.sample.viewmodels.counter

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal


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