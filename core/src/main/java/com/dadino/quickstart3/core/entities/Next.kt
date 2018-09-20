package com.dadino.quickstart3.core.entities


open class Next<S>(val state: S?,
				   val signals: List<Signal>,
				   val effects: List<SideEffect>) {

	companion object {
		fun <S> stateAndSignalsAndEffects(newState: S, signals: List<Signal>, effects: List<SideEffect>) = Next(newState, signals, effects)

		fun <S> stateAndSignalAndEffects(newState: S, signal: Signal, effects: List<SideEffect>) = Next(newState, listOf(signal), effects)

		fun <S> stateAndSignalsAndEffect(newState: S, signals: List<Signal>, effect: SideEffect) = Next(newState, signals, listOf(effect))

		fun <S> stateAndSignalAndEffect(newState: S, signal: Signal, effect: SideEffect) = Next(newState, listOf(signal), listOf(effect))

		fun <S> justState(newState: S) = Next(newState, listOf(), listOf())

		fun <S> justSignals(signals: List<Signal>) = Next<S>(null, signals, listOf())

		fun <S> justEffects(effects: List<SideEffect>) = Next<S>(null, listOf(), effects)

		fun <S> justSignal(signals: Signal) = Next<S>(null, listOf(signals), listOf())

		fun <S> justEffect(effects: SideEffect) = Next<S>(null, listOf(), listOf(effects))

		fun <S> stateAndSignals(newState: S, signals: List<Signal>) = Next(newState, signals, listOf())

		fun <S> stateAndEffects(newState: S, effects: List<SideEffect>) = Next(newState, listOf(), effects)

		fun <S> stateAndSignal(newState: S, signal: Signal) = Next(newState, listOf(signal), listOf())

		fun <S> stateAndEffect(newState: S, effect: SideEffect) = Next(newState, listOf(), listOf(effect))

		fun <S> signalsAndEffects(signals: List<Signal>, effects: List<SideEffect>) = Next<S>(null, signals, effects)

		fun <S> signalAndEffects(signal: Signal, effects: List<SideEffect>) = Next<S>(null, listOf(signal), effects)

		fun <S> signalsAndEffect(signals: List<Signal>, effect: SideEffect) = Next<S>(null, signals, listOf(effect))

		fun <S> signalAndEffect(signal: Signal, effect: SideEffect) = Next<S>(null, listOf(signal), listOf(effect))

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