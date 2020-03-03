package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.RxJavaSchedulerConfigurator
import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestState
import com.dadino.quickstart3.core.TestStateUpdater
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.utils.ConsoleLogger
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
		Mockito.`when`(updater.update(any(TestState::class.java), any(Event::class.java))).thenCallRealMethod()
		Mockito.`when`(updater.internalUpdate(any(TestState::class.java), any(Event::class.java))).thenCallRealMethod()

		quickLoop = QuickLoop("testloop", updater)
		quickLoop.enableLogging = true
		quickLoop.logger = ConsoleLogger()
		testObserver = TestObserver()

		Thread.sleep(100)

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