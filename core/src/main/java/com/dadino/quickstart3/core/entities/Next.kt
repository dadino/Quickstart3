package com.dadino.quickstart3.core.entities

open class Next<S : State<S>>(val state: S?,
							  val signals: List<Signal>,
							  val effects: List<SideEffect>) {

	companion object {

		fun <S : State<S>> stateAndSignalsAndEffects(newState: S, signals: List<Signal>, effects: List<SideEffect>) = Next(newState, signals, effects)

		fun <S : State<S>> stateAndSignalAndEffects(newState: S, signal: Signal, effects: List<SideEffect>) = Next(newState, listOf(signal), effects)

		fun <S : State<S>> stateAndSignalsAndEffect(newState: S, signals: List<Signal>, effect: SideEffect) = Next(newState, signals, listOf(effect))

		fun <S : State<S>> stateAndSignalAndEffect(newState: S, signal: Signal, effect: SideEffect) = Next(newState, listOf(signal), listOf(effect))

		fun <S : State<S>> justState(newState: S) = Next(newState, listOf(), listOf())

		fun <S : State<S>> justSignals(signals: List<Signal>) = Next<S>(null, signals, listOf())

		fun <S : State<S>> justEffects(effects: List<SideEffect>) = Next<S>(null, listOf(), effects)

		fun <S : State<S>> justSignal(signals: Signal) = Next<S>(null, listOf(signals), listOf())

		fun <S : State<S>> justEffect(effects: SideEffect) = Next<S>(null, listOf(), listOf(effects))

		fun <S : State<S>> stateAndSignals(newState: S, signals: List<Signal>) = Next(newState, signals, listOf())

		fun <S : State<S>> stateAndEffects(newState: S, effects: List<SideEffect>) = Next(newState, listOf(), effects)

		fun <S : State<S>> stateAndSignal(newState: S, signal: Signal) = Next(newState, listOf(signal), listOf())

		fun <S : State<S>> stateAndEffect(newState: S, effect: SideEffect) = Next(newState, listOf(), listOf(effect))

		fun <S : State<S>> signalsAndEffects(signals: List<Signal>, effects: List<SideEffect>) = Next<S>(null, signals, effects)

		fun <S : State<S>> signalAndEffects(signal: Signal, effects: List<SideEffect>) = Next<S>(null, listOf(signal), effects)

		fun <S : State<S>> signalsAndEffect(signals: List<Signal>, effect: SideEffect) = Next<S>(null, signals, listOf(effect))

		fun <S : State<S>> signalAndEffect(signal: Signal, effect: SideEffect) = Next<S>(null, listOf(signal), listOf(effect))

		fun <S : State<S>> noChanges() = Next<S>(null, listOf(), listOf())
	}

	override fun toString(): String {
		return "Next(state=$state, signals=$signals, effects=$effects)"
	}
}

class Start<S : State<S>>(val startState: S,
						  signals: List<Signal>,
						  effects: List<SideEffect>) : Next<S>(startState, signals, effects) {

	companion object {

		fun <S : State<S>> start(state: S,
								 signals: List<Signal> = listOf(),
								 effects: List<SideEffect> = listOf()
		) = Start(startState = state, signals = signals, effects = effects)
	}
}

class NextBuilder<T : State<T>> {

	private var state: T? = null
	private val effects: MutableList<SideEffect> = arrayListOf()
	private val signals: MutableList<Signal> = arrayListOf()

	fun build(): Next<T> = Next(state, signals, effects)

	fun state(state: T?): NextBuilder<T> {
		this.state = state
		return this
	}

	fun addEffect(effect: SideEffect?): NextBuilder<T> {
		if (effect != null) this.effects.add(effect)
		return this
	}

	fun addEffects(effects: List<SideEffect?>): NextBuilder<T> {
		this.effects.addAll(effects.filterIsInstance(SideEffect::class.java))
		return this
	}

	fun addSignal(signal: Signal?): NextBuilder<T> {
		if (signal != null) this.signals.add(signal)
		return this
	}

	fun addSignals(signals: List<Signal?>): NextBuilder<T> {
		this.signals.addAll(signals.filterIsInstance(Signal::class.java))
		return this
	}
}