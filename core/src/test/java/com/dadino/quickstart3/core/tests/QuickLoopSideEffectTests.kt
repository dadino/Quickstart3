package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAC_WAIT_TIME
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.QuickLoop
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopSideEffectTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
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