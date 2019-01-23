package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


interface SideEffectHandler {
	fun createObservable(eventRelay: Relay<Event>, effect: SideEffect): Disposable?
}


abstract class RxSingleSideEffectHandler<E : SideEffect>(
		private val disposeOnNewEffect: Boolean = false,
		private val subscribeOn: Scheduler = Schedulers.io(),
		private val observeOn: Scheduler = AndroidSchedulers.mainThread())
	: SideEffectHandler {
	private var internalDisposable: Disposable? = null
	override fun createObservable(eventRelay: Relay<Event>, effect: SideEffect): Disposable? {
		val disposable = if (checkClass(effect)) {
			effectToFlowable(effect as E)
					.subscribeOn(subscribeOn)
					.observeOn(observeOn)
					.toObservable()
					.subscribe(eventRelay)
		} else {
			null
		}

		if (disposable != null) {
			if (disposeOnNewEffect && internalDisposable?.isDisposed?.not() == true) {
				internalDisposable?.dispose()
			}
			internalDisposable = disposable
		}
		return disposable
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToFlowable(effect: E): Flowable<Event>
}

abstract class SingleSideEffectHandler<E : SideEffect>
	: SideEffectHandler {
	override fun createObservable(eventRelay: Relay<Event>, effect: SideEffect): Disposable? {
		return if (checkClass(effect)) {
			Single.fromCallable {
				effectToEvent(effect as E)
			}
					.toObservable()
					.subscribe(eventRelay)
		} else {
			null
		}
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToEvent(effect: E): Event

}