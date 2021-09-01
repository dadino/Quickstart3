package com.dadino.quickstart3.core.components

import androidx.lifecycle.*
import com.dadino.quickstart3.core.entities.*
import io.reactivex.Observable
import kotlin.reflect.KClass

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
			sideEffectHandlers = getSideEffectHandlers(),
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

	fun receiveEvent(event: Event) {
		loop.receiveEvent(event)
	}

	fun attachEventSource(tag: String, eventObservable: Observable<Event>) {
		loop.attachEventSource(tag, eventObservable)
	}


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