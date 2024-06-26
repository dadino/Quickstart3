package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.base.NoOpEvent
import com.dadino.quickstart3.core.RxJavaSchedulerConfigurator
import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestSignals
import com.dadino.quickstart3.core.TestState
import com.dadino.quickstart3.core.TestStateUpdater
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.components.OnConnectCallback
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Signal
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopSignalTests {

	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var testObserver: TestObserver<Signal>
	private val onConnectCallback = object : OnConnectCallback {
		override fun onConnect() {}
	}

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		val updater = TestStateUpdater(true)
		quickLoop = QuickLoop("testloop", updater, listOf(), onConnectCallback)
		quickLoop.enableLogging = true
		testObserver = TestObserver()

		Thread.sleep(100)

		quickLoop.signals
			.toObservable()
			.subscribe(testObserver)
	}

	@After
	fun tearDown() {
		testObserver.dispose()
		quickLoop.disconnect()
	}

	@Test
	fun sendEvent_propagateSignal() {

		//WHEN
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()
	}

	@Test
	fun sendEventWithNoopEvent_propagateSignalIfNotNoopEvent() {
		//WHEN
		quickLoop.receiveEvent(NoOpEvent)
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()
	}

	@Test
	fun sendEventWithUnusedEvent_propagateSignalIfNotUnusedEvent() {
		//WHEN
		quickLoop.receiveEvent(TestEvents.UnusedEvent)
		quickLoop.receiveEvent(TestEvents.AskForSignal(1))
		quickLoop.receiveEvent(TestEvents.AskForSignal(2))

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it is TestSignals.ResponseSignal && it.number == 1 }
		testObserver.assertValueAt(1) { it is TestSignals.ResponseSignal && it.number == 2 }

		testObserver.assertNotComplete()
	}
}