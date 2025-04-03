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

/**
 * An interface for handling side effects within a MVI architecture.
 *
 * This interface defines the methods required to manage and react to side effects,
 * which are asynchronous operations that can produce events.
 */
interface SideEffectHandler {

  /**
   * Creates a Flowable that represents a side effect.
   *
   * @param effect The side effect to be represented by the Flowable.
   * @return A Pair containing a Boolean indicating whether a Flowable was created and the Flowable itself (or null if no Flowable was created).
   *         - If the first element of the Pair is `true`, the second element will contain a non-null Flowable.
   *         - If the first element of the Pair is `false`, the second element will be `null`.  This indicates that the side effect could not be represented as a Flowable, likely due to its type.
   */
	fun createFlowable(effect: SideEffect): Pair<Boolean, Flowable<Event>?>

  /**
   * Sets the provided [Disposable] to be managed by the current context (e.g., a ViewModel or Presenter).
   * This typically involves adding the disposable to a CompositeDisposable or a similar mechanism
   * that ensures it is properly disposed of when the context is no longer active.
   *
   * This prevents memory leaks by ensuring that RxJava subscriptions are terminated when the
   * subscribing component is destroyed.
   *
   * @param disposable The [Disposable] representing an active RxJava subscription to be managed.
   */
	fun setDisposable(disposable: Disposable)
	fun onClear()
}

/**
 * An abstract class for handling side effects using RxJava's Flowable.
 * This class provides a base implementation for managing subscriptions and disposables
 * for side effects that produce a stream of events.
 *
 * @param E The type of SideEffect this handler can process.  Must extend [SideEffect].
 * @param disposeOnNewEffect  If `true`, any existing subscription is disposed when a new effect is handled. Defaults to `false`.  This is useful if subsequent effects of the same type should cancel prior ones.
 * @param disposeOnClear If `true`, all subscriptions are disposed when the handler is cleared (typically when the associated component is destroyed). Defaults to `true`.
 * @param subscribeOn The scheduler on which the effect's Flowable will subscribe. Defaults to `Schedulers.io()`.  This is usually where the side effect (e.g. network or database operation) occurs.
 * @param observeOn The scheduler on which the resulting events will be observed. Defaults to `AndroidSchedulers.mainThread()`. This is usually where UI updates occur.
 */
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

/**
 * An abstract base class for handling side effects of a specific type.
 *
 * This class simplifies the implementation of side effect handlers that deal with a single type of
 * side effect ([E]).  It manages a [CompositeDisposable] for handling subscriptions and offers
 * configuration options for disposable management.
 *
 * @param E The specific type of [SideEffect] this handler can process.
 * @param disposeOnNewEffect Whether to dispose of any existing subscriptions when a new effect is
 *   handled. Defaults to `true`.  If `false`, new subscriptions will be added to the existing
 *   [compositeDisposable] and all will be disposed on [onClear].  This is useful when an effect might
 *   require sequential emissions, for example.
 * @param disposeOnClear Whether to dispose of all subscriptions in the [compositeDisposable] when
 *   [onClear] is called. Defaults to `true`.  Set to `false` if some subscriptions should persist
 *   beyond the lifetime of the handler (e.g. globally shared subscriptions that are created by an
 *   effect).
 */
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