package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.RxJavaSchedulerConfigurator
import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestState
import com.dadino.quickstart3.core.TestStateUpdater
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopEventTests {

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
	fun sendEvent_eventReceived() {

		//WHEN
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)
		quickLoop.receiveEvent(TestEvents.Add1ToCounter)

		//THEN
		testObserver.awaitCount(2, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		verify(updater, Mockito.times(2)).start()
		verify(updater, Mockito.times(2)).update(any(TestState::class.java), any(Event::class.java))
	}
}