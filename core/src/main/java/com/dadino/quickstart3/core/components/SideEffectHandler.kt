package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.jakewharton.rxrelay2.Relay
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

interface SideEffectHandler {
	fun createObservable(eventRelay: Relay<Event>, effect: SideEffect): Disposable?
	fun onClear()
}

abstract class RxSingleSideEffectHandler<E : SideEffect>(
		private val disposeOnNewEffect: Boolean = false,
		private val disposeOnClear: Boolean = true,
		private val subscribeOn: Scheduler = Schedulers.io(),
		private val observeOn: Scheduler = AndroidSchedulers.mainThread())
	: SideEffectHandler {

	private var compositeDisposable: CompositeDisposable = CompositeDisposable()
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
			if (disposeOnNewEffect) {
				compositeDisposable.clear()
			}
			compositeDisposable.add(disposable)
		}
		return disposable
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToFlowable(effect: E): Flowable<Event>

	override fun onClear() {
		if (disposeOnClear) compositeDisposable.clear()
	}
}

abstract class SingleSideEffectHandler<E : SideEffect>(
		private val disposeOnNewEffect: Boolean = true,
		private val disposeOnClear: Boolean = true
) : SideEffectHandler {

	private var compositeDisposable: CompositeDisposable = CompositeDisposable()

	override fun createObservable(eventRelay: Relay<Event>, effect: SideEffect): Disposable? {
		val disposable = if (checkClass(effect)) {
			Single.fromCallable {
				effectToEvent(effect as E)
			}
				.toObservable()
				.subscribe(eventRelay)
		} else {
			null
		}

		if (disposable != null) {
			if (disposeOnNewEffect) {
				compositeDisposable.clear()
			}
			compositeDisposable.add(disposable)
		}
		return disposable
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToEvent(effect: E): Event
	override fun onClear() {
		if (disposeOnClear) compositeDisposable.clear()
	}
}