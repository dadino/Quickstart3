package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.core.TestEvents
import com.dadino.quickstart3.core.TestUtils
import com.dadino.quickstart3.core.components.EventManager
import com.dadino.quickstart3.core.components.EventTransformer
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.utils.ConsoleLogger
import io.reactivex.Observable
import io.reactivex.observers.BaseTestConsumer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EventManagerTests {
	private lateinit var eventManager: EventManager
	@Before
	fun setup() {
		eventManager = EventManager()
		eventManager.logger = ConsoleLogger()
		Thread.sleep(100)
	}

	@After
	fun tearDown() {
	}

	@Test
	fun addEventSource_eventSourceAdded() {
		assertEquals(0, eventManager.getEventSources().size())

		eventManager.attachEventSource(Observable.empty())

		assertEquals(1, eventManager.getEventSources().size())
	}

	@Test
	fun addEventSources_eventSourcesAdded() {
		assertEquals(0, eventManager.getEventSources().size())

		eventManager.attachEventSources(listOf(Observable.empty(), Observable.empty()))

		assertEquals(2, eventManager.getEventSources().size())
	}

	@Test
	fun disposeInteractionEvents_interactionEventsCleared() {
		//GIVEN
		eventManager.attachEventSources(listOf(Observable.empty(), Observable.empty()))
		val testSubscriber = eventManager.interactionEvents().test()

		//WHEN
		testSubscriber.dispose()
		Thread.sleep(500)

		//THEN
		assertFalse(eventManager.getEventSources().isDisposed)
		assertEquals(0, eventManager.getEventSources().size())
	}

	@Test
	fun pushEvent_eventReceived() {
		//GIVEN
		val testObserver = eventManager.interactionEvents().test()

		//WHEN
		eventManager.receiveEvent(TestEvents.UnusedEvent)

		//THEN
		testObserver.awaitCount(1, BaseTestConsumer.TestWaitStrategy.SLEEP_10MS, TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0, TestEvents.UnusedEvent)
	}

	@Test
	fun pushNoopEvent_eventNotReceived() {
		//GIVEN
		val testObserver = eventManager.interactionEvents().test()

		//WHEN
		eventManager.receiveEvent(NoOpEvent)

		//THEN
		testObserver.awaitCount(1, BaseTestConsumer.TestWaitStrategy.SLEEP_10MS, 100)
		testObserver.assertEmpty()
	}

	@Test
	fun pushEventWithTransformer_eventTransformedAndReceived() {
		//GIVEN
		eventManager.eventTransformer = object : EventTransformer() {
			override fun transform(event: Event): Event? {
				return when (event) {
					is TestEvents.UnusedEvent -> TestEvents.Add1ToCounter
					else                      -> event
				}
			}
		}
		val testObserver = eventManager.interactionEvents().test()

		//WHEN
		eventManager.receiveEvent(TestEvents.UnusedEvent)

		//THEN
		testObserver.awaitCount(1, BaseTestConsumer.TestWaitStrategy.SLEEP_10MS, TestUtils.MAX_WAIT_TIME_FOR_OBSERVABLES)
		testObserver.assertValueAt(0, TestEvents.Add1ToCounter)
	}
}