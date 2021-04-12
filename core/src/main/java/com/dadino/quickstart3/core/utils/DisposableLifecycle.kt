package com.dadino.quickstart3.core.utils

import androidx.lifecycle.*
import io.reactivex.disposables.Disposable

object DisposableLifecycle {

	fun attachAtResumeDetachAtPause(lifecycleOwner: LifecycleOwner, createDisposable: () -> List<Disposable>) {

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

	fun attachAtStartDetachAtStop(lifecycleOwner: LifecycleOwner, createDisposable: () -> List<Disposable>) {
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

	fun attachAtCreateDetachAtDestroy(lifecycleOwner: LifecycleOwner, createDisposable: () -> List<Disposable>) {
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

abstract class DisposableLifecycleObserver(lifecycleOwner: LifecycleOwner, attachState: Lifecycle.State, private val createDisposable: () -> List<Disposable>) :
		DefaultLifecycleObserver {

	private var disposables: List<Disposable>? = null

	init {
		if (lifecycleOwner.lifecycle.currentState.isAtLeast(attachState)) {
			disposables = createDisposable()
		}
	}

	fun attach() {
		if (disposables == null) {
			disposables = createDisposable()
		}
	}

	fun detach() {
		disposables?.forEach { it.dispose() }
		disposables = null
	}
}
