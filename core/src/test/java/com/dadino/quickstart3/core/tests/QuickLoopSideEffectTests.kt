package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.*
import com.dadino.quickstart3.core.TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES
import com.dadino.quickstart3.core.TestUtils.any
import com.dadino.quickstart3.core.components.OnConnectCallback
import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.ConsoleLogger
import io.reactivex.observers.BaseTestConsumer.TestWaitStrategy
import io.reactivex.observers.TestObserver
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopSideEffectTests {

	private lateinit var quickLoop: QuickLoop<TestState>
	private lateinit var updater: TestStateUpdater
	private lateinit var testObserver: TestObserver<in State>
	private lateinit var sideEffectHandler: SetNumberEffectHandler
	private val onConnectCallback = object : OnConnectCallback {
		override fun onConnect() {}
	}

	@Before
	fun setup() {
		RxJavaSchedulerConfigurator.prepareRxJava()

		sideEffectHandler = Mockito.spy(SetNumberEffectHandler())

		updater = TestUtils.testUpdater()

		quickLoop = QuickLoop("testloop", updater, listOf(sideEffectHandler), onConnectCallback)
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
	fun sendEvent_startSideEffect() {

		//WHEN
		quickLoop.getStateFlow(TestState::class)
			.toObservable()
			.subscribe(testObserver)

		quickLoop.receiveEvent(TestEvents.AskForStartSideEffect(1))

		//THEN
		testObserver.awaitCount(1, TestWaitStrategy.SLEEP_10MS, MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0) { (it as TestState).number == 0 }
		testObserver.assertValueAt(1) { (it as TestState).number == 1 }
		testObserver.assertNotComplete()

		verify(sideEffectHandler).checkClass(any(TestEffects.SetNumber::class.java))
	}
}