package com.dadino.quickstart3.base

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

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
@Immutable
open class Event

@Parcelize
open class ParcelableEvent : Event(), Parcelable

@Parcelize
data object InitializeState : ParcelableEvent()

@Parcelize
data object NoOpEvent : ParcelableEvent()

@Parcelize
sealed class LifecycleEvent : ParcelableEvent() {
  data object OnCreate : LifecycleEvent()
  data object OnStart : LifecycleEvent()
  data object OnResume : LifecycleEvent()
  data object OnPause : LifecycleEvent()
  data object OnStop : LifecycleEvent()
  data object OnDestroy : LifecycleEvent()
}