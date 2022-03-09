package com.dadino.quickstart3.core.components

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.NoOpEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class EventManager : InteractionEventSource, DefaultLifecycleObserver {

	var eventListener: EventListener? = null
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
			.doOnNext { eventListener?.onEvent(it) }
			.doOnNext { if (enableLogging) Timber.d(">>> ${it.javaClass.simpleName} >>>") }
			.map { eventTransformer?.performTransform(it) ?: it }
			.filter { it !is NoOpEvent }
			.doOnDispose { compositeDisposable.clear() }
			.publish()
			.refCount()
	}

	fun attachEventSource(eventSource: Observable<Event>) {
		compositeDisposable.add(eventSource.subscribe(eventRelay))
	}

	fun attachEventSources(eventSources: List<Observable<Event>>) {
		eventSources.forEach {
			compositeDisposable.add(it.subscribe(eventRelay))
		}
	}

	fun receiveEvent(event: Event) {
		eventRelay.accept(event)
	}

	override fun interactionEvents(): Observable<Event> {
		return eventObservable
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	fun getEventSources() = compositeDisposable
}

interface EventListener {

	fun onEvent(event: Event)
}