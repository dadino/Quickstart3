package com.dadino.quickstart3.core

import com.dadino.quickstart3.core.components.QuickLoop
import com.dadino.quickstart3.core.entities.Event
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuickLoopTests {
	init {
		RxJavaSchedulerConfigurator.prepareRxJava()
	}

	@Test
	fun sendEvent_eventReceived() {

		//GIVEN
		val updater = TestStateUpdater(false)
		val quickLoop = QuickLoop("testloop", updater)
		val updaterSpy = Mockito.spy(updater)

		//WHEN
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		Thread.sleep(1000)
		verify(updaterSpy, Mockito.times(2)).internalUpdate(Any<TestState>().any(), Any<Event>().any())
	}

	@Test
	fun sendEvent_updateState() {

		//GIVEN
		val quickLoop = QuickLoop("testloop", TestStateUpdater(false))
		val testObserver = TestObserver<TestState>()

		//WHEN
		quickLoop.states
				.toObservable()
				.subscribe(testObserver)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)
		quickLoop.receiveEvent(TestEntities.Add1ToCounter)

		//THEN
		Thread.sleep(1000)
		testObserver.assertValueAt(0) { it.counter == 1 }
		testObserver.assertValueAt(1) { it.counter == 2 }

		testObserver.assertNotComplete()
	}

}