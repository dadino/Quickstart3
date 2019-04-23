package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter

interface AttachedComponent

interface SignalResponder : AttachedComponent {
	fun respondTo(signal: Signal): Boolean
}

interface StateRenderer : AttachedComponent {
	fun renderState(state: State): Boolean
}

interface ViewModelAttacher : AttachedComponent {
	fun attachAdditionalViewModels(): List<VMStarter>
}