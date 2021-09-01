package com.dadino.quickstart3.core.components

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

object WorkerLifecycle {

	fun doAtResume(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onResume(owner: LifecycleOwner) {
				work()
			}
		})
	}

	fun doAtPause(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onPause(owner: LifecycleOwner) {
				work()
			}
		})
	}

	fun doAtStart(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onStart(owner: LifecycleOwner) {
				work()
			}
		})
	}

	fun doAtStop(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onStop(owner: LifecycleOwner) {
				work()
			}
		})
	}

	fun doAtCreate(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onCreate(owner: LifecycleOwner) {
				work()
			}
		})
	}

	fun doAtDestroy(lifecycleOwner: LifecycleOwner, work: () -> Unit) {
		lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onDestroy(owner: LifecycleOwner) {
				work()
			}
		})
	}
}
