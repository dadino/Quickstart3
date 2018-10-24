package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


interface SideEffectHandler {
	fun createObservable(effect: SideEffect): Observable<Event>?
}


abstract class RxSingleSideEffectHandler<E : SideEffect>(
		private val autoDispose: Boolean = true,
		private val subscribeOn: Scheduler = Schedulers.io(),
		private val observeOn: Scheduler = AndroidSchedulers.mainThread())
	: SideEffectHandler {

	override fun createObservable(effect: SideEffect): Observable<Event>? {
		return if (checkClass(effect)) {
			effectToFlowable(effect as E)
					.subscribeOn(subscribeOn)
					.observeOn(observeOn)
					.toObservable()
		} else {
			null
		}
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToFlowable(effect: E): Flowable<Event>
}

abstract class SingleSideEffectHandler<E : SideEffect>
	: SideEffectHandler {
	override fun createObservable(effect: SideEffect): Observable<Event>? {
		return if (checkClass(effect)) {
			Single.fromCallable {
				effectToEvent(effect as E)
			}
					.toObservable()
		} else {
			null
		}
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToEvent(effect: E): Event

}