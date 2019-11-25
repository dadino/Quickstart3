package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.RxJavaSchedulerConfigurator
import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestSignals
import com.dadino.quickstart3.core.TestStateUpdater
import com.dadino.quickstart3.core.TestUtils.MAC_WAIT_TIME
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.entities.Signal
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopSignalTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
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
}