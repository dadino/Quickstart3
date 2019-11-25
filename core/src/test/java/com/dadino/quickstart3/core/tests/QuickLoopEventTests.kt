package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAC_WAIT_TIME
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.*
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopEventTests {
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

		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
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
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
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
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
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
		quickLoop.receiveEvent(TestEvents.UnusedEvent)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEvent_propagateSignal() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<Signal>()

		//WHEN
		quickLoop.signals
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEventWithNoopEvent_propagateSignalIfNotNoopEvent() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<Signal>()

		//WHEN
		quickLoop.signals
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(NoOpEvent)
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEventWithUnusedEvent_propagateSignalIfNotUnusedEvent() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<Signal>()

		//WHEN
		quickLoop.signals
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEvents.UnusedEvent)
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()

		testObserver.dispose()
	}

	@Test
	fun sendEvent_startSideEffect() {

		//GIVEN
		val sideEffectHandler = Mockito.spy(StartSideEffectHandler())

		val quickLoop = QuickLoop("testloop", TestStateUpdater(false), listOf(sideEffectHandler))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEvents.AskForStartSideEffect(1))

		//THEN
		testObserver.awaitCount(1, TestWaitStrategy.SLEEP_10MS, MAC_WAIT_TIME)
		testObserver.assertValueAt(0) { it.number == 1 }
		testObserver.assertNotComplete()

		testObserver.dispose()

		verify(sideEffectHandler).checkClass(any(TestEffects.StartSideEffect::class.java))
	}
}