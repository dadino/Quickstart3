package com.dadino.quickstart3.core.components

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.LifecycleEvent
import com.dadino.quickstart3.core.entities.State
import io.reactivex.Observable
import kotlin.reflect.KClass

/**
 * Abstract base class for ViewModels, providing a structured way to manage state and handle events using a QuickLoop.
 *
 * This class integrates with the Android lifecycle through [DefaultLifecycleObserver] and uses a [QuickLoop]
 * to manage the application's state ([STATE]) and process events.  Subclasses should define the state
 * update logic via [updater] and provide any necessary [SideEffectHandler]s.
 *
 * @param STATE The type of state managed by the ViewModel.  Must implement the [State] interface.
 */
abstract class BaseViewModel<STATE : State> : ViewModel(), DefaultLifecycleObserver {

  var onConnectCallback: OnConnectCallback? = null
  private val internalOnConnectCallback = object : OnConnectCallback {
	override fun onConnect() {
	  onConnectCallback?.onConnect()
	}
  }
  private val loop: QuickLoop<STATE> by lazy {
	QuickLoop(
	  loopName = javaClass.simpleName,
	  sideEffectHandlers = getSideEffectHandlers() + (if (this is SideEffectHandlerProvider) this.provideAdditionalSideEffectHandlers() else listOf()),
	  updater = updater(),
	  onConnectCallback = internalOnConnectCallback
	)
  }

  protected fun enableLogging(enableLogging: Boolean) {
	loop.enableLogging = enableLogging
  }

  override fun onCleared() {
	super.onCleared()
	loop.disconnect()
  }

  override fun onCreate(owner: LifecycleOwner) {
	if (wantOnCreateEvent()) loop.receiveEvent(LifecycleEvent.OnCreate)
  }

  override fun onStart(owner: LifecycleOwner) {
	if (wantOnStartEvent()) loop.receiveEvent(LifecycleEvent.OnStart)
  }

  override fun onResume(owner: LifecycleOwner) {
	if (wantOnResumeEvent()) loop.receiveEvent(LifecycleEvent.OnResume)
  }

  override fun onPause(owner: LifecycleOwner) {
	if (wantOnPauseEvent()) loop.receiveEvent(LifecycleEvent.OnPause)
  }

  override fun onStop(owner: LifecycleOwner) {
	if (wantOnStopEvent()) loop.receiveEvent(LifecycleEvent.OnStop)
  }

  override fun onDestroy(owner: LifecycleOwner) {
	if (wantOnDestroyEvent()) loop.receiveEvent(LifecycleEvent.OnDestroy)
  }

  /**
   * Receives an event and dispatches it to the main event loop.
   *
   * This function serves as an intermediary, taking an [Event] object and forwarding it to
   * the `receiveEvent` method of the internal `loop` object, which is responsible for
   * processing events within the application.
   *
   * @param event The [Event] object to be processed.
   */
  fun receiveEvent(event: Event) {
	loop.receiveEvent(event)
  }

  /**
   * Attaches an event source to the main loop.
   *
   * This function subscribes an observable of events to the main loop, associating it with a given tag.
   * The loop will handle emitted events from the observable, allowing for centralized event processing.
   *
   * @param tag A unique string identifier for the event source.  This tag can be used later to manage or detach the source.
   * @param eventObservable An Observable stream that emits [Event] objects.  The loop will subscribe to this observable and process any emitted events.
   */
  fun attachEventSource(tag: String, eventObservable: Observable<Event>) {
	loop.attachEventSource(tag, eventObservable)
  }

  /**
   * Returns the current state of the state machine.
   *
   * This function provides access to the underlying state machine's current state.  It effectively delegates the call to the `loop.currentState()` method.
   *
   * @return A [StateFlow] representing the current state of the state machine.  You can use this to observe state changes or retrieve the current state value.
   */
  fun currentState() = loop.currentState()
  fun subStates() = loop.getSubStates()
  fun subState(subStateClass: KClass<out State>) = loop.getSubState(subStateClass)
  fun statesFlows() = loop.getStateFlows()
  fun signalsFlows() = loop.signals

  abstract fun updater(): Updater<STATE>

  abstract fun getSideEffectHandlers(): List<SideEffectHandler>

  fun canReceiveEvents() = loop.canReceiveEvents
  open protected fun wantOnCreateEvent() = false
  open protected fun wantOnStartEvent() = false
  open protected fun wantOnResumeEvent() = false
  open protected fun wantOnPauseEvent() = false
  open protected fun wantOnStopEvent() = false
  open protected fun wantOnDestroyEvent() = false
}

/**
 * A provider interface for additional side effect handlers.
 *
 * Implementations of this interface should provide a list of [SideEffectHandler] instances
 * that can be used to extend the functionality of a system that handles side effects.
 * This allows for modularization and easy addition of new side effect handling logic without
 * modifying the core system.
 */
interface SideEffectHandlerProvider {
  /**
   * Provides a list of additional [SideEffectHandler] instances that should be
   * registered with the system. These handlers will be responsible for handling
   * side effects emitted by the application's business logic.
   *
   * Note: The handlers returned by this function are added to the default set of
   * handlers.  If a side effect type is handled by both a default handler and
   * one provided by this function, the handler provided by this function will
   * take precedence.
   *
   * @return A list of [SideEffectHandler] instances.  An empty list indicates that
   *         no additional handlers are required.
   */
  fun provideAdditionalSideEffectHandlers(): List<SideEffectHandler>
}