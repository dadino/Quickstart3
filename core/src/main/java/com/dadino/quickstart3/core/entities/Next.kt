package com.dadino.quickstart3.core.entities


class Next<S>(val state: S?,
			  val signals: List<Signal<Any>>,
			  val effects: List<SideEffect>) {
	companion object {
		fun <S> next(newState: S,
					 signals: List<Signal<Any>> = listOf(),
					 effects: List<SideEffect> = listOf()
		) = Next(state = newState, signals = signals, effects = effects)

		fun dispatchSignals(signals: List<Signal<Any>>) = Next(null, signals, listOf())

		fun dispatchEffects(effects: List<SideEffect>) = Next(null, listOf(), effects)

		fun noChanges() = Next(null, listOf(), listOf())
	}
}