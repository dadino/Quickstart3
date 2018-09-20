package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


interface SideEffectHandler {
	fun handle(effect: SideEffect): Boolean
	fun dispose()
	fun connectTo(relay: Relay<Event>)
}

abstract class RxSingleSideEffectHandler<E : SideEffect>(
		private val autoDispose: Boolean = true,
		private val subscribeOn: Scheduler = Schedulers.io(),
		private val observeOn: Scheduler = AndroidSchedulers.mainThread())
	: SideEffectHandler {

	private var eventRelay: Relay<Event>? = null
	private var disposable: Disposable? = null

	override fun handle(effect: SideEffect): Boolean {
		if (checkClass(effect)) {
			disposable?.dispose()
			disposable = effectToFlowable(effect as E)
					.subscribeOn(subscribeOn)
					.observeOn(observeOn)
					.subscribeBy(onNext = { event -> eventRelay?.accept(event) })
			return true
		} else return false
	}

	override fun dispose() {
		eventRelay = null
		if (autoDispose) disposable?.dispose()
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToFlowable(effect: E): Flowable<Event>

	override fun connectTo(relay: Relay<Event>) {
		eventRelay = relay
	}
}

abstract class SingleSideEffectHandler<E : SideEffect>
	: SideEffectHandler {

	private var eventRelay: Relay<Event>? = null

	override fun handle(effect: SideEffect): Boolean {
		if (checkClass(effect)) {
			eventRelay?.accept(effectToEvent(effect as E))
			return true
		} else return false
	}

	override fun dispose() {
		eventRelay = null
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToEvent(effect: E): Event

	override fun connectTo(relay: Relay<Event>) {
		eventRelay = relay
	}
}