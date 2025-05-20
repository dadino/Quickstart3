package com.dadino.quickstart3.core.components

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.NoOpEvent
import com.dadino.quickstart3.core.utils.QuickLogger
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Manages and dispatches events within the application.  It acts as a central hub for
 * routing events from various sources to registered listeners, and optionally transforms
 * events before dispatch.
 *
 * Key Features:
 *  - **Event Aggregation:**  Collects events from multiple reactive streams (Observables)
 *    and merges them into a single stream.
 *  - **Event Dispatch:**  Forwards events to a registered [EventListener] for handling.
 *  - **Event Transformation:**  Allows for modifying events using an [EventTransformer]
 *    before dispatch.  This can be used for actions like data enrichment, filtering,
 *    or reformatting.
 *  - **Lifecycle Management:**  Uses [CompositeDisposable] to manage subscriptions to
 *    event sources, ensuring resources are released when no longer needed.
 *  - **Logging:** Optionally logs events for debugging purposes, enabled by setting a `tag`.
 *  - **Testing:** Provides methods to inspect internal state for testing purposes.
 *
 * Usage:
 *  1. Create an `EventManager` instance.
 *  2. Attach event sources using [attachEventSource] or [attachEventSources].  Each source
 *     is identified by a unique key.  Sources should be Observables emitting [Event] objects.
 *  3. (Optional) Set an [EventListener] to receive dispatched events.
 *  4. (Optional) Set an [EventTransformer] to modify events before dispatch.
 *  5. If logging is desired, set the `tag` property to a non-null value.
 *  6. Events from attached sources, as well as events manually submitted via [receiveEvent],
 *     will be processed and dispatched to the listener.
 *  7.  Observe interaction events using [interactionEvents] if needed for UI binding or other purposes.
 */
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
  private val eventSourceMap: MutableMap<String, Disposable> = mutableMapOf()

  private val eventObservable: Observable<Event> by lazy {
	eventRelay
	  .filter { it !is NoOpEvent }
	  .doOnNext { eventListener?.onEvent(it) }
	  .doOnNext { if (enableLogging) QuickLogger.tag(tag).d { ">>> ${it.javaClass.simpleName} >>>" } }
	  .map { eventTransformer?.performTransform(it) ?: it }
	  .filter { it !is NoOpEvent }
	  .doOnDispose { compositeDisposable.clear() }
	  .publish()
	  .refCount()
  }

  /**
   * Attaches an event source to the event relay, allowing events from the source to be processed.
   *
   * This function manages the lifecycle of event sources, ensuring that only one source is active
   * for a given key at any time.  If a source with the same key already exists, it will be
   * removed and unsubscribed before the new source is attached.
   *
   * @param key A unique string identifier for the event source. This key is used to track and manage
   *   the source within the system.  Subsequent calls with the same key will replace the previous source.
   * @param eventSource An Observable emitting [Event] objects.  Events emitted by this Observable will be
   *   relayed to the `eventRelay` after being attached.  This Observable should be managed externally
   *   (i.e., its lifecycle should not be tied to the lifecycle of this class beyond attachment and
   *   detachment).  When the Observable completes or emits an error, the subscription will be managed
   *   internally, but the Observable itself will remain unchanged.
   */
  fun attachEventSource(key: String, eventSource: Observable<Event>) {
	eventSourceMap[key]?.let { compositeDisposable.remove(it) }

	val disposable = eventSource.subscribe(eventRelay)

	eventSourceMap[key] = disposable
	compositeDisposable.add(disposable)
  }

  /**
   * Attaches multiple event sources to the system.
   *
   * This function iterates through a map of event sources, where the keys are identifiers
   * for the sources and the values are Observables of `Event` objects. For each entry
   * in the map, it calls the [attachEventSource] function to register the event source
   * with the specified identifier.
   *
   * @param eventSources A map of event source identifiers to Observables of `Event` objects.
   *                     Each entry represents an event source to be attached.
   *
   * @see attachEventSource
   */
  fun attachEventSources(eventSources: Map<String, Observable<Event>>) {
	eventSources.forEach { entry ->
	  attachEventSource(entry.key, entry.value)
	}
  }

  /**
   * Receives an [Event] and relays it to the [eventRelay].
   *
   * This function acts as a central point for handling incoming events.  It takes an [Event]
   * object as input and then forwards it to a reactive stream represented by [eventRelay].
   * This stream likely manages the distribution of events to various observers within the system.
   *
   * @param event The [Event] object to be received and relayed.  This represents an occurrence
   *              or action within the system that needs to be processed or communicated.
   */
  fun receiveEvent(event: Event) {
	eventRelay.accept(event)
  }

  /**
   * Returns an Observable that emits interaction events (e.g., clicks, touches) on this view.
   *
   * This Observable will emit events of type [Event], which represent user interactions with the view.
   *  The specific types of events emitted depend on the interactions that are being observed.
   * For instance, if observing touch events, the Observable would emit events of a touch-related type.
   *
   * @return An Observable emitting interaction events.
   */
  override fun interactionEvents(): Observable<Event> {
	return eventObservable
  }

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun getEventSources() = compositeDisposable
}

/**
 * Interface for receiving events.
 */
interface EventListener {

  fun onEvent(event: Event)
}