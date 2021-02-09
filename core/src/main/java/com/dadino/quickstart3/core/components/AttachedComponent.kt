package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.*

interface AttachedComponent

interface SignalResponder : AttachedComponent {

	fun respondTo(signal: Signal): Boolean
}

interface StateRenderer : AttachedComponent {

	fun renderState(state: State<*>): Boolean
}

interface ViewModelAttacher : AttachedComponent {

	fun attachAdditionalViewModels(): List<VMStarter>
}