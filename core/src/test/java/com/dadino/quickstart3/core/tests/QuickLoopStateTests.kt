package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopStateTests {
	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var updater: TestStateUpdater
	private lateinit var testObserver: TestObserver<TestState>

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		updater = Mockito.mock(TestStateUpdater::class.java)
		Mockito.`when`(updater.start()).thenCallRealMethod()
		Mockito.`when`(updater.update(TestUtils.any(TestState::class.java), TestUtils.any(Event::class.java))).thenCallRealMethod()
		Mockito.`when`(updater.internalUpdate(TestUtils.any(TestState::class.java), TestUtils.any(Event::class.java))).thenCallRealMethod()

		quickLoop = QuickLoop("testloop", updater)
		testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
	}

	@After
	fun tearDown() {
		testObserver.dispose()
		quickLoop.disconnect()
	}


	@Test
	fun sendEvent_updateState() {

		//WHEN
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()
	}

	@Test
	fun sendEventWithNoopEvent_updateStateIfNotNoopEvent() {
		//WHEN
		quickLoop.receiveEvent(NoOpEvent)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()
	}

	@Test
	fun sendEventWithUnusedEvent_updateStateIfNotUnusedEvent() {

		//WHEN
		quickLoop.receiveEvent(TestEvents.UnusedEvent)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()
	}
}