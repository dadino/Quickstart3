package com.dadino.quickstart3.core.entities


open class Next<S : State>(val state: S?,
						   val signals: List<Signal>,
						   val effects: List<SideEffect>) {

	companion object {
		fun <S : State> stateAndSignalsAndEffects(newState: S, signals: List<Signal>, effects: List<SideEffect>) = Next(newState, signals, effects)

		fun <S : State> stateAndSignalAndEffects(newState: S, signal: Signal, effects: List<SideEffect>) = Next(newState, listOf(signal), effects)

		fun <S : State> stateAndSignalsAndEffect(newState: S, signals: List<Signal>, effect: SideEffect) = Next(newState, signals, listOf(effect))

		fun <S : State> stateAndSignalAndEffect(newState: S, signal: Signal, effect: SideEffect) = Next(newState, listOf(signal), listOf(effect))

		fun <S : State> justState(newState: S) = Next(newState, listOf(), listOf())

		fun <S : State> justSignals(signals: List<Signal>) = Next<S>(null, signals, listOf())

		fun <S : State> justEffects(effects: List<SideEffect>) = Next<S>(null, listOf(), effects)

		fun <S : State> justSignal(signals: Signal) = Next<S>(null, listOf(signals), listOf())

		fun <S : State> justEffect(effects: SideEffect) = Next<S>(null, listOf(), listOf(effects))

		fun <S : State> stateAndSignals(newState: S, signals: List<Signal>) = Next(newState, signals, listOf())

		fun <S : State> stateAndEffects(newState: S, effects: List<SideEffect>) = Next(newState, listOf(), effects)

		fun <S : State> stateAndSignal(newState: S, signal: Signal) = Next(newState, listOf(signal), listOf())

		fun <S : State> stateAndEffect(newState: S, effect: SideEffect) = Next(newState, listOf(), listOf(effect))

		fun <S : State> signalsAndEffects(signals: List<Signal>, effects: List<SideEffect>) = Next<S>(null, signals, effects)

		fun <S : State> signalAndEffects(signal: Signal, effects: List<SideEffect>) = Next<S>(null, listOf(signal), effects)

		fun <S : State> signalsAndEffect(signals: List<Signal>, effect: SideEffect) = Next<S>(null, signals, listOf(effect))

		fun <S : State> signalAndEffect(signal: Signal, effect: SideEffect) = Next<S>(null, listOf(signal), listOf(effect))

		fun <S : State> noChanges() = Next<S>(null, listOf(), listOf())
	}

	override fun toString(): String {
		return "Next(state=$state, signals=$signals, effects=$effects)"
	}
}

class Start<S : State>(val startState: S,
					   signals: List<Signal>,
					   effects: List<SideEffect>) : Next<S>(startState, signals, effects) {
	companion object {
		fun <S : State> start(state: S,
							  signals: List<Signal> = listOf(),
							  effects: List<SideEffect> = listOf()
		) = Start(startState = state, signals = signals, effects = effects)
	}
}