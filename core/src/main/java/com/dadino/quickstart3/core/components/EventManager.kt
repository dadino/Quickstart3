package com.dadino.quickstart3.core.components

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class EventManager(lifecycleOwner: LifecycleOwner) : InteractionEventSource, DefaultLifecycleObserver {
	init {
		lifecycleOwner.lifecycle.addObserver(this)
	}

	var tag: String? = null
		set(value) {
			field = value
			enableLogging = value != null
		}
	private var enableLogging = false
	var eventTransformer: EventTransformer? = null
	var eventCollection: Observable<Event>? = Observable.empty()

	private var eventDisposable: Disposable? = null
	private val eventRelay: PublishRelay<Event> by lazy { PublishRelay.create<Event>() }
	private val eventObservable: Observable<Event> by lazy {
		Observable.merge(eventCollection, eventRelay)
				.filter { it !is NoOpEvent }
				.doOnNext { log { ">>> ${it.javaClass.simpleName} >>>" } }
				.map { eventTransformer?.performTransform(it) ?: it }
				.filter { it !is NoOpEvent }
				.publish()
				.refCount()
	}

	override fun onResume(owner: LifecycleOwner) {
		connect()
	}

	override fun onPause(owner: LifecycleOwner) {
		disconnect()
	}

	private fun connect() {
		eventDisposable = interactionEvents().subscribe()
	}

	private fun disconnect() {
		eventDisposable?.dispose()
	}

	fun interactionEventsConsumer(): PublishRelay<Event> {
		return eventRelay
	}

	fun receiveInteractionEvent(event: Event) {
		interactionEventsConsumer().accept(event)
	}

	override fun interactionEvents(): Observable<Event> {
		return eventObservable
	}


	private fun log(createMessage: () -> String) {
		if (enableLogging) Log.d(tag, createMessage())
	}
}