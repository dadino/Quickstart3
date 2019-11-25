package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.RxJavaSchedulerConfigurator
import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestState
import com.dadino.quickstart3.core.TestStateUpdater
import com.dadino.quickstart3.core.TestUtils.MAC_WAIT_TIME
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.NoOpEvent
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopStateTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
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
}