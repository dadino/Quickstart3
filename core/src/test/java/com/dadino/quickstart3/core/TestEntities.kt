package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.*

data class TestState(
		val counter: Int = 0,
		val number: Int = 0
) : State() {

	private val isGreaterThan3 = counter > 3

	override fun getStatesToPropagate(isInitialization: Boolean, previousState: State): List<State> {
		check(previousState is TestState)
		val list = arrayListOf<State>()
		list.addAll(super.getStatesToPropagate(isInitialization, previousState))
		if (previousState.isGreaterThan3 != isGreaterThan3 || isInitialization)
			list.add(TestSubState(isCounterGreaterThan3 = isGreaterThan3))
		return list
	}
}

data class TestSubState(
		val isCounterGreaterThan3: Boolean
) : State()

open class TestStateUpdater(useLogging: Boolean = true) : Updater<TestState>(useLogging) {

	override fun start(): Start<TestState> {
		return Start.start(TestState())
	}

	override fun update(previous: TestState, event: Event): Next<TestState> {
		return when (event) {
			TestEvents.Add1ToCounter              -> Next.justState(previous.copy(counter = previous.counter + 1))
			is TestEvents.AskForSignal            -> Next.justSignal(TestSignals.ResponseSignal(event.number))
			is TestEvents.AskForStartSideEffect   -> Next.justEffect(TestEffects.SetNumber(event.number))
			is TestEvents.AdaptToSideEffectResult -> Next.justState(previous.copy(number = event.number))
			else                                  -> Next.noChanges()
		}
	}

	override fun getSubStateClasses(): List<Class<out State>> {
		return listOf(
			TestState::class.java,
			TestSubState::class.java
		)
	}
}

sealed class TestEvents {
	class AskForSignal(val number: Int) : Event()
	class AskForStartSideEffect(val number: Int) : Event()
	class AdaptToSideEffectResult(val number: Int) : Event()

	object Add1ToCounter : Event()
	object UnusedEvent : Event()
}

sealed class TestSignals {
	class ResponseSignal(val number: Int) : Signal()
}

sealed class TestEffects {
	class SetNumber(val number: Int) : SideEffect()
}

class SetNumberEffectHandler : SingleSideEffectHandler<TestEffects.SetNumber>() {

	override fun checkClass(effect: SideEffect): Boolean {
		return effect is TestEffects.SetNumber
	}

	override fun effectToEvent(effect: TestEffects.SetNumber): Event {
		return TestEvents.AdaptToSideEffectResult(effect.number)
	}
}