package com.dadino.quickstart3.core.components

import io.reactivex.disposables.Disposable

interface DisposableLifecycleHolder {
	fun attachDisposableToResumePause(createDisposable: () -> Disposable)
	fun attachDisposableToStartStop(createDisposable: () -> Disposable)
	fun attachDisposableToCreateDestroy(createDisposable: () -> Disposable)
}