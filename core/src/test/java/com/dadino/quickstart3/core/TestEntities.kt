package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.State


data class TestState(val counter: Int = 0) : State()

open class TestStateUpdater(useLogging: Boolean = true) : Updater<TestState>(useLogging) {
	override fun start(): Start<TestState> {
		return Start.start(TestState())
	}

	override fun update(previous: TestState, event: Event): Next<TestState> {
		return when (event) {
			TestEntities.Add1ToCounter -> Next.justState(previous.copy(counter = previous.counter + 1))
			else                       -> Next.noChanges()
		}
	}
}

sealed class TestEntities {
	object Add1ToCounter : Event()
	object UnusedEvent : Event()
}