package com.dadino.quickstart3.core.fragments

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.DisposableLifecycleHolder
import com.dadino.quickstart3.core.components.EventManager
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseDialogFragment : DialogFragment(), DisposableLifecycleHolder {
	protected val eventManager: EventManager = EventManager()

	open fun renderState(state: State) {}
	open fun respondTo(signal: Signal) {}

	protected fun <S : State, T : BaseViewModel<S>> attachViewModel(viewModel: T, minimumState: Lifecycle.State = Lifecycle.State.RESUMED) {
		viewModel.attachEventSource(eventManager.interactionEvents())
		attachToLifecycle(viewModel, minimumState)
	}

	private fun <S : State, T : BaseViewModel<S>> attachToLifecycle(viewModel: T, minimumState: Lifecycle.State) {
		when (minimumState) {
			Lifecycle.State.RESUMED -> {
				attachDisposableToResumePause { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToResumePause { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToStartStop { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToCreateDestroy { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
			}
			else                    -> throw RuntimeException("minimumState $minimumState not supported")
		}
	}

	override fun attachDisposableToCreateDestroy(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtCreateDetachAtDestroy(this, createDisposable)
	}

	override fun attachDisposableToStartStop(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtStartDetachAtStop(this, createDisposable)
	}

	override fun attachDisposableToResumePause(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtResumeDetachAtPause(this, createDisposable)
	}
}