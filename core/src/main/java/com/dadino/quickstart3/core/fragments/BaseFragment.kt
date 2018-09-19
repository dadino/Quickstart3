package com.dadino.quickstart3.core.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.InteractionEventSourceHandler
import com.dadino.quickstart3.core.components.SimpleInteractionEventSource
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment(), SimpleInteractionEventSource, DisposableLifecycleHolder {

	lateinit var interactionEventSourceHandler: InteractionEventSourceHandler

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return internalInitViews(inflater, container, savedInstanceState)
	}

	override fun onDestroyView() {
		interactionEventSourceHandler.disconnect()
		super.onDestroyView()
	}

	private fun internalInitViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = initViews(inflater, container, savedInstanceState)
		interactionEventSourceHandler = object : InteractionEventSourceHandler() {
			override fun collectUserActions(): Observable<Event> {
				return this@BaseFragment.collectUserActions()
			}

			override fun interceptUserAction(action: Event): Event {
				return this@BaseFragment.interceptUserAction(action)
			}
		}
		interactionEventSourceHandler.connect()
		return view
	}

	abstract fun initViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

	protected fun <S : Any, T : BaseViewModel<S>> attachViewModel(viewModel: T, minimumState: Lifecycle.State = Lifecycle.State.RESUMED, render: (S) -> Unit): T {
		attachToLifecycle(viewModel, minimumState, render)

		return viewModel
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