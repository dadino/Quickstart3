package com.dadino.quickstart3.core

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dadino.quickstart3.core.components.Actionable
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.UserActionsHandler
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseActivity : AppCompatActivity(), Actionable, DisposableLifecycleHolder {

	override lateinit var userActionsHandler: UserActionsHandler

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		internalInitViews()
	}


	private fun internalInitViews() {
		initViews()
		userActionsHandler = object : UserActionsHandler() {
			override fun collectUserActions(): Observable<UserAction> {
				return this@BaseActivity.collectUserActions()
			}

			override fun interceptUserAction(action: UserAction): UserAction {
				return this@BaseActivity.interceptUserAction(action)
			}
		}
		userActionsHandler.connect()
	}

	override fun onDestroy() {
		userActionsHandler.disconnect()
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