package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.*


data class TestState(
		val counter: Int = 0,
		val number: Int = 0
) : State()

open class TestStateUpdater(useLogging: Boolean = true) : Updater<TestState>(useLogging) {
	override fun start(): Start<TestState> {
		return Start.start(TestState())
	}

	override fun update(previous: TestState, event: Event): Next<TestState> {
		return when (event) {
			TestEvents.Add1ToCounter              -> Next.justState(previous.copy(counter = previous.counter + 1))
			is TestEvents.AskForSignal            -> Next.justSignal(TestSignals.ResponseSignal(event.number))
			is TestEvents.AskForStartSideEffect   -> Next.justEffect(TestEffects.StartSideEffect(event.number))
			is TestEvents.AdaptToSideEffectResult -> Next.justState(previous.copy(number = event.number))
			else                                  -> Next.noChanges()
		}
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
	class StartSideEffect(val number: Int) : SideEffect()
}

class StartSideEffectHandler : SingleSideEffectHandler<TestEffects.StartSideEffect>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is TestEffects.StartSideEffect
	}

	override fun effectToEvent(effect: TestEffects.StartSideEffect): Event {
		return TestEvents.AdaptToSideEffectResult(effect.number)
	}
}