package com.dadino.quickstart3.core.components

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.NoOpEvent
import com.dadino.quickstart3.core.utils.ILogger
import com.dadino.quickstart3.core.utils.LogcatLogger
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class EventManager : InteractionEventSource, DefaultLifecycleObserver {

	var logger: ILogger = LogcatLogger()
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

	private fun log(createMessage: () -> String) {
		if (enableLogging) logger.log(tag ?: "EventManager", createMessage())
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	fun getEventSources() = compositeDisposable
}