package com.dadino.quickstart3.core.components

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dadino.quickstart3.core.entities.StateCommand
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.utils.toAsync
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseViewModel<STATE> : ViewModel() {
	private val reducer by lazy { reducer() }
	private val initialState by lazy { initialState() }
	private val userActionRelay: PublishRelay<UserAction> by lazy { PublishRelay.create<UserAction>() }
	private val userActionFlowable: Flowable<UserAction>  by lazy {
		userActionRelay.doOnNext { Log.d(className(), "<---- ${it.javaClass.simpleName}") }
				.retry()
				.doOnError { onError(it) }
				.toFlowable(BackpressureStrategy.BUFFER)
				.toAsync()
				.replayingShare()
	}

	private val stateCommandRelay: PublishRelay<StateCommand> by lazy {
		PublishRelay.create<StateCommand>()
	}

	val states: Flowable<STATE>  by lazy {
		stateCommandRelay
				.doOnNext { Log.d(className(), "----> ${it.javaClass.simpleName}") }
				.scan(initialState) { previous: STATE, command: StateCommand ->
					Log.d(className(), "Reducing with command: ${command.javaClass.simpleName}")
					reducer.reduce(previous, command)
				}
				.doOnNext { Log.d(className(), "STATE: $it") }
				.toFlowable(BackpressureStrategy.LATEST)
				.toAsync()
				.distinctUntilChanged()
				.replay(1)
				.autoConnect(0)
	}

	init {
		userActionFlowable.subscribeBy(onNext = {
			reactToUserAction(state(), it)
		})
	}

	fun receiveUserAction(action: UserAction) {
		userActionsConsumer().accept(action)
	}

	fun userActionsConsumer(): Consumer<UserAction> = userActionRelay

	protected fun commandConsumer(): Consumer<StateCommand> = stateCommandRelay

	protected fun pushCommand(command: StateCommand) {
		commandConsumer().accept(command)
	}

	fun state(): STATE {
		return states.blockingMostRecent(initialState).first()
	}

	protected abstract fun reducer(): Reducer<STATE>

	abstract fun reactToUserAction(currentState: STATE, action: UserAction)

	protected abstract fun initialState(): STATE

	protected fun onError(error: Throwable) {
		error.printStackTrace()
	}

	private fun className(): String {
		return javaClass.simpleName
	}

	@Deprecated(message = "Do not use this method anymore, use the attribute states", replaceWith = ReplaceWith("states"), level = DeprecationLevel.ERROR)
	fun states(): Flowable<STATE> {
		throw RuntimeException("Do not use this method anymore, use the attribute states")
	}

	@Deprecated("Replace with a Reducer", replaceWith = ReplaceWith(""), level = DeprecationLevel.ERROR)
	fun reduce(previous: STATE, command: StateCommand): STATE {
		throw RuntimeException("Replace with a Reducer")
	}
}