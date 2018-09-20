package com.dadino.quickstart3.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.DisposableLifecycleHolder
import com.dadino.quickstart3.core.components.InteractionEventSourceHandler
import com.dadino.quickstart3.core.components.SimpleInteractionEventSource
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseActivity : AppCompatActivity(), SimpleInteractionEventSource, DisposableLifecycleHolder {
	override lateinit var interactionEventSourceHandler: InteractionEventSourceHandler

	abstract fun initViews()
	abstract fun renderState(state: State)
	abstract fun respondTo(signal: Signal)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		internalInitViews()
	}

	private fun internalInitViews() {
		initViews()
		interactionEventSourceHandler = object : InteractionEventSourceHandler() {
			override fun collectInteractionEvents(): Observable<Event> {
				return this@BaseActivity.collectInteractionEvents()
			}

			override fun interceptInteractionEvents(event: Event): Event {
				return this@BaseActivity.interceptInteractionEvents(event)
			}
		}
		interactionEventSourceHandler.connect()
	}

	override fun onDestroy() {
		interactionEventSourceHandler.disconnect()
		super.onDestroy()
	}


	protected fun <S : State, T : BaseViewModel<S>> attachViewModel(viewModel: T, minimumState: Lifecycle.State = Lifecycle.State.RESUMED) {
		attachToLifecycle(viewModel, minimumState)
	}

	private fun <S : State, T : BaseViewModel<S>> attachToLifecycle(viewModel: T, minimumState: Lifecycle.State) {
		when (minimumState) {
			Lifecycle.State.RESUMED -> {
				attachDisposableToResumePause { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToResumePause { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
				attachDisposableToResumePause { interactionEvents().subscribeBy(onNext = { viewModel.receiveEvent(it) }) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToStartStop { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
				attachDisposableToStartStop { interactionEvents().subscribeBy(onNext = { viewModel.receiveEvent(it) }) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy { viewModel.states().subscribeBy(onNext = { renderState(it) }) }
				attachDisposableToCreateDestroy { viewModel.signals().subscribeBy(onNext = { respondTo(it) }) }
				attachDisposableToCreateDestroy { interactionEvents().subscribeBy(onNext = { viewModel.receiveEvent(it) }) }
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