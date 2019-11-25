package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.entities.Start
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
	}

	@Test
	fun sendEvent_eventReceived() {

		//GIVEN
		val updater = Mockito.mock(TestStateUpdater::class.java)
		Mockito.`when`(updater.start()).thenReturn(Start.start(TestState()))
		Mockito.`when`(updater.update(any(TestState::class.java), any(Event::class.java))).thenReturn(Next.justState(TestState()))
		Mockito.`when`(updater.internalUpdate(any(TestState::class.java), any(Event::class.java))).thenCallRealMethod()

		val quickLoop = QuickLoop("testloop", updater)
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)

		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, 15000)
		verify(updater, Mockito.times(2)).start()
		verify(updater, Mockito.times(2)).update(any(TestState::class.java), any(Event::class.java))

		testObserver.dispose()
	}

	@Test
	fun sendEvent_updateState() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, 15000)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEventWithNoopEvent_updateStateIfNotNoopEvent() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(NoOpEvent)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, 15000)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEventWithUnusedEvent_updateStateIfNotUnusedEvent() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEntities.UnusedEvent)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, 15000)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}