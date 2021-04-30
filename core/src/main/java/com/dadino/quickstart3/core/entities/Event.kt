package com.dadino.quickstart3.core.entities

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