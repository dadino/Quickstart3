package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

interface SimpleInteractionEventSource : InteractionEventSource {
	var eventManager: EventManager

	fun interactionEventsConsumer(): PublishRelay<Event> {
		return eventManager.interactionEventsConsumer()
	}

	fun receiveInteractionEvents(event: Event) {
		eventManager.receiveInteractionEvent(event)
	}

	override fun interactionEvents(): Observable<Event> {
		return eventManager.interactionEvents()
	}

	fun collectInteractionEvents(): Observable<Event> {
		return Observable.empty()
	}

	fun setEventInterceptor(eventTransformer: EventTransformer) {
		eventManager.eventTransformer = eventTransformer
	}
}