package com.dadino.quickstart3.core.components

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class EventManager : InteractionEventSource, DefaultLifecycleObserver {

	var tag: String? = null
		set(value) {
			field = value
			enableLogging = value != null
		}
	private var enableLogging = false
	var eventTransformer: EventTransformer? = null

	private val compositeDisposable: CompositeDisposable = CompositeDisposable()
	private val eventRelay: PublishRelay<Event> = PublishRelay.create<Event>()

	private val eventObservable: Observable<Event> by lazy {
		eventRelay
				.filter { it !is NoOpEvent }
				.doOnNext { log { ">>> ${it.javaClass.simpleName} >>>" } }
				.map { eventTransformer?.performTransform(it) ?: it }
				.filter { it !is NoOpEvent }
				.doOnDispose { compositeDisposable.clear() }
				.publish()
				.refCount()
	}

	fun attachEventSource(events: Observable<Event>) {
		compositeDisposable.add(events.subscribe(eventRelay))
	}

	fun attachEventSources(events: List<Observable<Event>>) {
		events.forEach {
			compositeDisposable.add(it.subscribe(eventRelay))
		}
	}

	fun receiveEvent(event: Event) {
		eventRelay.accept(event)
	}

	override fun interactionEvents(): Observable<Event> {
		return eventObservable
	}

	private fun log(createMessage: () -> String) {
		if (enableLogging) Log.d(tag, createMessage())
	}
}