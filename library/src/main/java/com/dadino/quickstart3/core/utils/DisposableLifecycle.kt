package com.dadino.quickstart3.core.utils

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

object DisposableLifecycle {

	fun attachToResumePause(lifecycleOwner: LifecycleOwner, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
				DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.RESUMED, createDisposable) {

			override fun onResume(owner: LifecycleOwner) {
				attach()
			}

			override fun onPause(owner: LifecycleOwner) {
				detach()
			}
		})
	}

	fun attachToStartStop(lifecycleOwner: LifecycleOwner, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
				DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.STARTED, createDisposable) {

			override fun onStart(owner: LifecycleOwner) {
				attach()
			}

			override fun onStop(owner: LifecycleOwner) {
				detach()
			}
		})
	}

	fun attachToCreateDestroy(lifecycleOwner: LifecycleOwner, createDisposable: () -> Disposable) {
		lifecycleOwner.lifecycle.addObserver(object :
				DisposableLifecycleObserver(lifecycleOwner, Lifecycle.State.CREATED, createDisposable) {

			override fun onCreate(owner: LifecycleOwner) {
				attach()
			}

			override fun onDestroy(owner: LifecycleOwner) {
				detach()
			}
		})
	}
}

abstract class DisposableLifecycleObserver(lifecycleOwner: LifecycleOwner, attachState: Lifecycle.State, private val createDisposable: () -> Disposable) : DefaultLifecycleObserver {
	private var disposable: Disposable? = null

	init {
		if (lifecycleOwner.lifecycle.currentState.isAtLeast(attachState)) {
			disposable = createDisposable()
		}
	}

	fun attach() {
		if (disposable == null) disposable = createDisposable()
	}

	fun detach() {
		disposable?.dispose()
		disposable = null
	}
}