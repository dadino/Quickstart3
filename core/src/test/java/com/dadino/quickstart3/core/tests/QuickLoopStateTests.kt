package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.TestUtils.testUpdater
import com.dadino.quickstart3.core.components.OnConnectCallback
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.ConsoleLogger
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopStateTests {

	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var updater: TestStateUpdater
	private lateinit var testObserver: TestObserver<in State>
	private val onConnectCallback = object : OnConnectCallback {
		override fun onConnect() {}
	}

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		updater = testUpdater()

		quickLoop = QuickLoop("testloop", updater, listOf(), onConnectCallback)
		quickLoop.enableLogging = true
		quickLoop.logger = ConsoleLogger()
		testObserver = TestObserver()

		Thread.sleep(100)

		quickLoop.states
			.first()
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
		testObserver.assertValueAt(0) { (it as TestState).counter == 1 }
		testObserver.assertValueAt(1) { (it as TestState).counter == 2 }

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
		testObserver.assertValueAt(0) { (it as TestState).counter == 1 }
		testObserver.assertValueAt(1) { (it as TestState).counter == 2 }

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
		testObserver.assertValueAt(0) { (it as TestState).counter == 1 }
		testObserver.assertValueAt(1) { (it as TestState).counter == 2 }

		testObserver.assertNotComplete()
	}
}