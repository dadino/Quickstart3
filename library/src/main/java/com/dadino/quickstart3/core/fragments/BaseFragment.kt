package com.dadino.quickstart3.core.fragments

import androidx.lifecycle.Lifecycle
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dadino.quickstart3.core.components.Actionable
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.UserActionsHandler
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseFragment : Fragment(), Actionable, DisposableLifecycleHolder {

	override lateinit var userActionsHandler: UserActionsHandler

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return internalInitViews(inflater, container, savedInstanceState)
	}

	override fun onDestroyView() {
		userActionsHandler.disconnect()
		super.onDestroyView()
	}

	private fun internalInitViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = initViews(inflater, container, savedInstanceState)
		userActionsHandler = object : UserActionsHandler() {
			override fun collectUserActions(): Observable<UserAction> {
				return this@BaseFragment.collectUserActions()
			}

			override fun interceptUserAction(action: UserAction): UserAction {
				return this@BaseFragment.interceptUserAction(action)
			}
		}
		userActionsHandler.connect()
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
				attachDisposableToResumePause { userActions().subscribe(viewModel.userActionsConsumer()) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop { viewModel.states.subscribeBy(onNext = { render(it) }) }
				attachDisposableToStartStop { userActions().subscribe(viewModel.userActionsConsumer()) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy { viewModel.states.subscribeBy(onNext = { render(it) }) }
				attachDisposableToCreateDestroy { userActions().subscribe(viewModel.userActionsConsumer()) }
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