package com.dadino.quickstart3.base

/**
 * A base class for events.
 *
 * This class serves as a foundational type for representing events within the application.
 * Subclasses should extend this class to define specific event types and their associated data.
 *
 * Example:
 * ```kotlin
 * class UserLoggedInEvent(val userId: String) : Event()
 * ```
 */
open class Event

object InitializeState : Event()
object NoOpEvent : Event()

sealed class LifecycleEvent : Event() {
	object OnCreate : Event()
	object OnStart : Event()
	object OnResume : Event()
	object OnPause : Event()
	object OnStop : Event()
	object OnDestroy : Event()
}