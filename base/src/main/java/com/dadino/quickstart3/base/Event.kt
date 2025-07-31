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

data object InitializeState : Event()
data object NoOpEvent : Event()

sealed class LifecycleEvent : Event() {
  data object OnCreate : Event()
  data object OnStart : Event()
  data object OnResume : Event()
  data object OnPause : Event()
  data object OnStop : Event()
  data object OnDestroy : Event()
}