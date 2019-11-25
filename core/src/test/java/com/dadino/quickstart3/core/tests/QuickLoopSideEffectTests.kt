package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
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
class QuickLoopSideEffectTests {
	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var updater: TestStateUpdater
	private lateinit var testObserver: TestObserver<TestState>
	private lateinit var sideEffectHandler: StartSideEffectHandler

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		sideEffectHandler = Mockito.spy(StartSideEffectHandler())

		updater = Mockito.mock(TestStateUpdater::class.java)
		Mockito.`when`(updater.start()).thenCallRealMethod()
		Mockito.`when`(updater.update(TestUtils.any(TestState::class.java), TestUtils.any(Event::class.java))).thenCallRealMethod()
		Mockito.`when`(updater.internalUpdate(TestUtils.any(TestState::class.java), TestUtils.any(Event::class.java))).thenCallRealMethod()

		quickLoop = QuickLoop("testloop", updater, listOf(sideEffectHandler))
		testObserver = TestObserver<TestState>()

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
	fun sendEvent_startSideEffect() {

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEvents.AskForStartSideEffect(1))

		//THEN
		testObserver.awaitCount(1, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { it.number == 1 }
		testObserver.assertNotComplete()

		verify(sideEffectHandler).checkClass(any(TestEffects.StartSideEffect::class.java))
	}
}