package com.dadino.quickstart3.core.entities

/**
 * Represents the result of processing an [Event] in a [Updater].
 *
 * A [Next] instance encapsulates the potential changes to the [Updater]'s state,
 * as well as any signals or side effects that should be triggered.
 *
 * @param S The type of the state managed by the [Updater].
 * @property state The new state of the [Updater] after processing the event. If `null`, the state remains unchanged.
 * @property signals A list of [Signal]s to be emitted as a result of processing the event.  Signals represent notifications or events within the state machine.
 * @property effects A list of [SideEffect]s to be executed as a result of processing the event. Side effects represent actions outside the state machine, like network calls or UI updates.
 */
open class Next<S : State>(
  val state: S?,
  val signals: List<Signal>,
  val effects: List<SideEffect>
) {

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

class Start<S : State>(
  val startState: S,
  signals: List<Signal>,
  effects: List<SideEffect>
) : Next<S>(startState, signals, effects) {
  fun addEffects(additionalEffects: List<SideEffect>): Start<S> {
	return Start(startState, signals, effects + additionalEffects)
  }

  fun addSignals(additionalSignals: List<Signal>): Start<S> {
	return Start(startState, signals + additionalSignals, effects)
  }

  companion object {

	fun <S : State> start(
	  state: S,
	  signals: List<Signal> = listOf(),
	  effects: List<SideEffect> = listOf()
	) = Start(startState = state, signals = signals, effects = effects)
  }
}

/**
 * Builder for [Next] object that represents the next state of a state machine.
 * Provides methods to set the next state, add side effects, and add signals.
 *
 * @param T The type of the state.
 */
class NextBuilder<T : State> {

  private var state: T? = null
  private val effects: MutableList<SideEffect> = arrayListOf()
  private val signals: MutableList<Signal> = arrayListOf()

  /**
   * Builds a new [Next] instance with the current state, signals, and effects.
   *
   * @return A new [Next] instance.
   */
  fun build(): Next<T> = Next(state, signals, effects)

  /**
   * Sets the next state.
   *
   * This function allows you to define the state that the state
   * should transition to upon completing the current state's action.  A `null` state
   * typically indicates that the state should halt or enter a terminal state.
   *
   * @param state The next state to transition to.  Can be `null` to indicate the end of the state's execution.
   * @return The current [NextBuilder] instance, allowing for method chaining.
   */
  fun state(state: T?): NextBuilder<T> {
	this.state = state
	return this
  }

  /**
   * Adds a side effect to the builder.
   *
   * Side effects represent actions that need to be performed outside the scope of the current operation,
   * such as UI updates, network requests, or data persistence.  If the provided effect is null, it is ignored.
   *
   * @param effect The [SideEffect] to add to the builder.  May be null, in which case no effect is added.
   * @return The updated [NextBuilder] instance, allowing for chained calls.
   */
  fun addEffect(effect: SideEffect?): NextBuilder<T> {
	if (effect != null) this.effects.add(effect)
	return this
  }

  /**
   * Adds a list of side effects to the builder.  Null values within the list are ignored.
   *
   * @param effects A list of [SideEffect]s to add.  Null values will be filtered out.
   * @return The current [NextBuilder] instance with the added side effects.
   */
  fun addEffects(effects: List<SideEffect?>): NextBuilder<T> {
	this.effects.addAll(effects.filterIsInstance(SideEffect::class.java))
	return this
  }

  /**
   * Adds a [Signal] to the list of signals.
   *
   * @param signal The signal to add.  If `null`, the signal is ignored.
   * @return The current [NextBuilder] instance for chaining.
   */
  fun addSignal(signal: Signal?): NextBuilder<T> {
	if (signal != null) this.signals.add(signal)
	return this
  }

  /**
   * Adds a list of signals to the builder.  Filters out any null values and only adds instances of [Signal].
   *
   * @param signals A list of [Signal] objects (or nulls) to be added.
   * @return The current [NextBuilder] instance with the added signals.
   */
  fun addSignals(signals: List<Signal?>): NextBuilder<T> {
	this.signals.addAll(signals.filterIsInstance(Signal::class.java))
	return this
  }
}