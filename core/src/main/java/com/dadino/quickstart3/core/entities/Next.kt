package com.dadino.quickstart3.core.entities


open class Next<S>(val state: S?,
				   val signals: List<Signal>,
				   val effects: List<SideEffect>) {

	companion object {
		fun <S> next(newState: S,
					 signals: List<Signal> = listOf(),
					 effects: List<SideEffect> = listOf()
		) = Next(state = newState, signals = signals, effects = effects)

		fun <S> justState(newState: S) = Next(newState, listOf(), listOf())

		fun <S> justSignals(signals: List<Signal>) = Next<S>(null, signals, listOf())

		fun <S> justEffects(effects: List<SideEffect>) = Next<S>(null, listOf(), effects)

		fun <S> justSignal(signals: Signal) = Next<S>(null, listOf(signals), listOf())

		fun <S> justEffect(effects: SideEffect) = Next<S>(null, listOf(), listOf(effects))

		fun <S> noChanges() = Next<S>(null, listOf(), listOf())
	}

	override fun toString(): String {
		return "Next(state=$state, signals=$signals, effects=$effects)"
	}
}

class Start<S>(state: S,
			   signals: List<Signal>,
			   effects: List<SideEffect>) : Next<S>(state, signals, effects) {
	companion object {
		fun <S> start(state: S,
					  signals: List<Signal> = listOf(),
					  effects: List<SideEffect> = listOf()
		) = Start(state = state, signals = signals, effects = effects)
	}
}