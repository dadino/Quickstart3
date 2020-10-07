package com.dadino.quickstart3.core.utils

import androidx.lifecycle.*
import io.reactivex.disposables.Disposable

object DisposableLifecycle {

	fun attachAtResumeDetachAtPause(lifecycleOwner: LifecycleOwner, callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
													 DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.RESUMED, callback, createDisposable) {

			override fun onResume(owner: LifecycleOwner) {
				attach()
			}

			override fun onPause(owner: LifecycleOwner) {
				detach()
			}
		})
	}

	fun attachAtStartDetachAtStop(lifecycleOwner: LifecycleOwner, callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
													 DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.STARTED, callback, createDisposable) {

			override fun onStart(owner: LifecycleOwner) {
				attach()
			}

			override fun onStop(owner: LifecycleOwner) {
				detach()
			}
		})
	}

	fun attachAtCreateDetachAtDestroy(lifecycleOwner: LifecycleOwner, callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
													 DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.CREATED, callback, createDisposable) {

			override fun onCreate(owner: LifecycleOwner) {
				attach()
			}

			override fun onDestroy(owner: LifecycleOwner) {
				detach()
			}
		})
	}
}

abstract class DisposableLifecycleObserver(lifecycleOwner: LifecycleOwner, attachState: Lifecycle.State, private val callback: AttachDetachCallback?, private val createDisposable: () -> Disposable) :
		DefaultLifecycleObserver {

	private var disposable: Disposable? = null

	init {
		if (lifecycleOwner.lifecycle.currentState.isAtLeast(attachState)) {
			disposable = createDisposable()
		}
	}

	fun attach() {
		if (disposable == null) {
			disposable = createDisposable()
			callback?.onAttach()
		}
	}

	fun detach() {
		disposable?.dispose()
		disposable = null
		callback?.onDetach()
	}
}

interface AttachDetachCallback {

	fun onAttach()
	fun onDetach()
}