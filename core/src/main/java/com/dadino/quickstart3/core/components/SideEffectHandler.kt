package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.entities.SideEffect
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

interface SideEffectHandler {

	fun createFlowable(effect: SideEffect): Pair<Boolean, Flowable<Event>?>
	fun setDisposable(disposable: Disposable)
	fun onClear()
}

abstract class RxSingleSideEffectHandler<E : SideEffect>(
	private val disposeOnNewEffect: Boolean = false,
	private val disposeOnClear: Boolean = true,
	private val subscribeOn: Scheduler = Schedulers.io(),
	private val observeOn: Scheduler = AndroidSchedulers.mainThread()
) : SideEffectHandler {

	private var compositeDisposable: CompositeDisposable = CompositeDisposable()
	override fun createFlowable(effect: SideEffect): Pair<Boolean, Flowable<Event>?> {
		val disposable = if (checkClass(effect)) {
			effectToFlowable(effect as E)
				.subscribeOn(subscribeOn)
				.observeOn(observeOn)
		} else {
			null
		}

		if (disposable != null) {
			if (disposeOnNewEffect) {
				compositeDisposable.clear()
			}
		}
		return Pair(overrideEffectHandled(effect) || disposable != null, disposable)
	}

	override fun setDisposable(disposable: Disposable) {
		compositeDisposable.add(disposable)
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToFlowable(effect: E): Flowable<Event>

	open fun overrideEffectHandled(effect: SideEffect): Boolean {
		return false
	}

	override fun onClear() {
		if (disposeOnClear) compositeDisposable.clear()
	}
}

abstract class SingleSideEffectHandler<E : SideEffect>(
	private val disposeOnNewEffect: Boolean = true,
	private val disposeOnClear: Boolean = true
) : SideEffectHandler {

	private var compositeDisposable: CompositeDisposable = CompositeDisposable()

	override fun createFlowable(effect: SideEffect): Pair<Boolean, Flowable<Event>?> {
		val disposable = if (checkClass(effect)) {
			Single.fromCallable {
				effectToEvent(effect as E)
			}
				.toFlowable()
		} else {
			null
		}

		if (disposable != null) {
			if (disposeOnNewEffect) {
				compositeDisposable.clear()
			}
		}
		return Pair(overrideEffectHandled(effect) || disposable != null, disposable)
	}

	override fun setDisposable(disposable: Disposable) {
		compositeDisposable.add(disposable)
	}

	abstract fun checkClass(effect: SideEffect): Boolean

	protected abstract fun effectToEvent(effect: E): Event

	open fun overrideEffectHandled(effect: SideEffect): Boolean {
		return false
	}

	override fun onClear() {
		if (disposeOnClear) compositeDisposable.clear()
	}
}