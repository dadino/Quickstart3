package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import io.reactivex.Observable

/**
 * Represents a source of user interaction events.
 *
 * This interface defines a contract for classes that provide a stream of user interaction events as an Observable.
 *  Implementations of this interface are responsible for observing and emitting events related to user interactions
 *  with a specific UI element or a collection of UI elements.
 *
 *  The type of the events emitted is determined by the generic type parameter [Event].
 *  This allows for flexibility in representing different types of user interactions,
 *  such as clicks, touches, gestures, or any other custom event.
 */
interface InteractionEventSource {
	fun interactionEvents(): Observable<Event>
}