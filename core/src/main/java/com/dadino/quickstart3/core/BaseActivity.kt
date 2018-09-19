package com.dadino.quickstart3.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.InteractionEventSourceHandler
import com.dadino.quickstart3.core.components.SimpleInteractionEventSource
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity(), SimpleInteractionEventSource, DisposableLifecycleHolder {

	lateinit var interactionEventSourceHandler: InteractionEventSourceHandler

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		internalInitViews()
	}

	private fun internalInitViews() {
		initViews()
		interactionEventSourceHandler = object : InteractionEventSourceHandler() {
			override fun collectUserActions(): Observable<Event> {
				return this@BaseActivity.collectUserActions()
			}

			override fun interceptUserAction(action: Event): Event {
				return this@BaseActivity.interceptUserAction(action)
			}
		}
		interactionEventSourceHandler.connect()
	}

	override fun onDestroy() {
		interactionEventSourceHandler.disconnect()
		super.onDestroy()
	}

	abstract fun initViews()

	protected fun <S : Any, T : BaseViewModel<S>> attachViewModel(viewModel: T, minimumState: Lifecycle.State = Lifecycle.State.RESUMED, render: (S) -> Unit) {
		attachToLifecycle(viewModel, minimumState, render)
	}

	private fun <S : Any, T : BaseViewModel<S>> attachToLifecycle(viewModel: T, minimumState: Lifecycle.State, render: (S) -> Unit) {
		when (minimumState) {
			Lifecycle.State.RESUMED -> {
				attachDisposableToResumePause { viewModel.states.subscribeBy(onNext = { render(it) }) }
				attachDisposableToResumePause { interactionEvents().subscribe(viewModel.userActionsConsumer()) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop { viewModel.states.subscribeBy(onNext = { render(it) }) }
				attachDisposableToStartStop { interactionEvents().subscribe(viewModel.userActionsConsumer()) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy { viewModel.states.subscribeBy(onNext = { render(it) }) }
				attachDisposableToCreateDestroy { interactionEvents().subscribe(viewModel.userActionsConsumer()) }
			}
			else                    -> throw RuntimeException("minimumState $minimumState not supported")
		}
	}

	override fun attachDisposableToCreateDestroy(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToCreateDestroy(this, createDisposable)
	}

	override fun attachDisposableToStartStop(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToStartStop(this, createDisposable)
	}

	override fun attachDisposableToResumePause(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToResumePause(this, createDisposable)
	}
}