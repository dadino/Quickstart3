package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.Event
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SimpleInteractionEventSource : InteractionEventSource {
	val userActionsHandler: InteractionEventSourceHandler

	fun userActionsConsumer(): PublishRelay<Event> {
		return userActionsHandler.userActionsConsumer()
	}

	fun receiveUserAction(userAction: Event) {
		userActionsHandler.receiveUserAction(userAction)
	}

	override fun interactionEvents(): Observable<Event> {
		return userActionsHandler.interactionEvents()
	}

	fun collectUserActions(): Observable<Event> {
		return Observable.empty()
	}

	fun interceptUserAction(action: Event): Event {
		return action
	}
}

abstract class InteractionEventSourceHandler : InteractionEventSource {
	private val userActionsRelay: PublishRelay<Event> by lazy { PublishRelay.create<Event>() }

	private val userActions: Observable<Event> by lazy {
		Observable.merge(collectUserActions(), userActionsRelay)
				.doOnNext { Log.d("UserAction", "Original: $it") }
				.map { interceptUserAction(it) }
				.doOnNext { Log.d("UserAction", "Intercepted: $it") }
				.publish()
				.refCount()
	}

	private var userActionsDisposable: Disposable? = null

	fun connect() {
		userActionsDisposable = interactionEvents().subscribe()
	}

	fun disconnect() {
		userActionsDisposable?.dispose()
	}

	open fun collectUserActions(): Observable<Event> {
		return Observable.empty()
	}

	fun userActionsConsumer(): PublishRelay<Event> {
		return userActionsRelay
	}

	fun receiveUserAction(userAction: Event) {
		userActionsConsumer().accept(userAction)
	}

	final override fun interactionEvents(): Observable<Event> {
		return userActions
	}

	protected open fun interceptUserAction(action: Event): Event {
		return action
	}

}