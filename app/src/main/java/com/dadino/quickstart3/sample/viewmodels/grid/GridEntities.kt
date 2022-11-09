package com.dadino.quickstart3.sample.viewmodels.grid

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal


sealed class GridEffect : SideEffect() {
}

sealed class GridSignal : Signal() {
}

sealed class GridEvent : Event() {
	class OnPlusClicked(val index: Int) : Event()
	class OnMinusClicked(val index: Int) : Event()
}