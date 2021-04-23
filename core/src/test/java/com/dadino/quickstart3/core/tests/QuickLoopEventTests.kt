package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.OnConnectCallback
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.ConsoleLogger
import io.reactivex.Observable
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.*
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopEventTests {

	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var updater: TestStateUpdater
	private lateinit var testObserver: TestObserver<in State>

	private val onConnectCallback = object : OnConnectCallback {
		override fun onConnect() {}
	}

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		updater = TestUtils.testUpdater()

		quickLoop = QuickLoop("testloop", updater, listOf(), onConnectCallback)
		quickLoop.enableLogging = true
		quickLoop.logger = ConsoleLogger()
		testObserver = TestObserver()

		Thread.sleep(100)

		quickLoop.getStateFlow(TestState::class)
			.toObservable()
			.subscribe(testObserver)
	}

	@After
	fun tearDown() {
		testObserver.dispose()
		quickLoop.disconnect()
	}

	@Test
	fun sendEvent_eventReceived() {

		//WHEN
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		verify(updater, Mockito.times(1)).start()
		verify(updater, Mockito.times(2)).update(any(TestState::class.java), any(Event::class.java))
	}

	@Test
	fun addEventSource_eventSourceAdded() {
		Assert.assertEquals(0, quickLoop.getEventSources().size())

		quickLoop.attachEventSource(Observable.empty())

		Assert.assertEquals(1, quickLoop.getEventSources().size())
	}

	@Test
	fun newQuickLoop_hasState() {
		val quickLoop = QuickLoop("testloop", updater, listOf(), onConnectCallback)
		assertNotNull(quickLoop.currentState())
	}
}