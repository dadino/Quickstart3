package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.Event
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SimpleInteractionEventSource : InteractionEventSource {
	var interactionEventSourceHandler: InteractionEventSourceHandler

	fun interactionEventsConsumer(): PublishRelay<Event> {
		return interactionEventSourceHandler.interactionEventsConsumer()
	}

	fun receiveInteractionEvents(userAction: Event) {
		interactionEventSourceHandler.receiveInteractionEvent(userAction)
	}

	override fun interactionEvents(): Observable<Event> {
		return interactionEventSourceHandler.interactionEvents()
	}

	fun collectInteractionEvents(): Observable<Event> {
		return Observable.empty()
	}

	fun interceptInteractionEvents(action: Event): Event {
		return action
	}
}

abstract class InteractionEventSourceHandler : InteractionEventSource {
	private val interactionEventsRelay: PublishRelay<Event> by lazy { PublishRelay.create<Event>() }

	private val interactionEvents: Observable<Event> by lazy {
		Observable.merge(collectInteractionEvents(), interactionEventsRelay)
				.doOnNext { Log.d("UserAction", "Original: $it") }
				.map { interceptInteractionEvents(it) }
				.doOnNext { Log.d("UserAction", "Intercepted: $it") }
				.publish()
				.refCount()
	}

	private var interactionEventsDisposable: Disposable? = null

	fun connect() {
		interactionEventsDisposable = interactionEvents().subscribe()
	}

	fun disconnect() {
		interactionEventsDisposable?.dispose()
	}

	open fun collectInteractionEvents(): Observable<Event> {
		return Observable.empty()
	}

	fun interactionEventsConsumer(): PublishRelay<Event> {
		return interactionEventsRelay
	}

	fun receiveInteractionEvent(userAction: Event) {
		interactionEventsConsumer().accept(userAction)
	}

	final override fun interactionEvents(): Observable<Event> {
		return interactionEvents
	}

	protected open fun interceptInteractionEvents(event: Event): Event {
		return event
	}

}