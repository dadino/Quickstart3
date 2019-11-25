package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.QuickLoop
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
	}


	@Before
	fun prepare() {
		MockitoAnnotations.initMocks(this)
	}

	@Test
	fun sendEvent_eventReceived() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)

		//THEN
		//testObserver.assertValue(TestState())

		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		testObserver.assertValue { it.counter == 1 }
		testObserver.assertNotComplete()

		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		testObserver.assertValue { it.counter == 2 }
		testObserver.assertNotComplete()
	}
}