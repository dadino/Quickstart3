package com.dadino.quickstart3.core.components

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.utils.DisposableLifecycle
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy


class AttachedComponentController(private val lifecycleOwner: LifecycleOwner,
								  private val eventManager: EventManager,
								  private val renderFun: (State) -> Unit,
								  private val respondFun: (Signal) -> Unit
) : DefaultLifecycleObserver {

	private val vmAttachers: ArrayList<ViewModelAttacher> = arrayListOf()
	private val stateRenderers: ArrayList<StateRenderer> = arrayListOf()
	private val signalResponders: ArrayList<SignalResponder> = arrayListOf()

	fun attachViewModels(VMS: List<VMStarter>) {
		val list: ArrayList<VMStarter> = arrayListOf()
		list.addAll(VMS)
		vmAttachers.forEach { list.addAll(it.attachAdditionalViewModels()) }
		list.forEach { attachViewModel(it.viewModel, it.minimumState) }
	}

	private fun renderState(state: State, render: (State) -> Unit) {
		var rendered = false
		stateRenderers.forEach {
			val r = it.renderState(state)
			if (r) rendered = true
		}
		if (rendered.not()) render(state)
	}

	private fun respondTo(signal: Signal, respond: (Signal) -> Unit) {
		var responded = false
		signalResponders.forEach {
			val r = it.respondTo(signal)
			if (r) responded = true
		}
		if (responded.not()) respond(signal)
	}

	private fun renderStateInternal(state: State) {
		renderState(state) { renderFun(state) }
	}

	private fun respondToInternal(signal: Signal) {
		respondTo(signal) { respondFun(signal) }
	}

	private fun addViewModelAttacher(vmAttacher: ViewModelAttacher) {
		vmAttachers.add(vmAttacher)
	}

	private fun addStateRenderer(stateRenderer: StateRenderer) {
		stateRenderers.add(stateRenderer)
	}

	private fun addSignalResponder(signalResponder: SignalResponder) {
		signalResponders.add(signalResponder)
	}

	private fun clear() {
		vmAttachers.clear()
		signalResponders.clear()
		stateRenderers.clear()
	}

	private fun attachViewModel(viewModel: BaseViewModel<*>, minimumState: Lifecycle.State = Lifecycle.State.RESUMED) {
		viewModel.attachEventSource(eventManager.interactionEvents())
		attachToLifecycle(viewModel, minimumState)
	}

	private fun attachToLifecycle(viewModel: BaseViewModel<*>, minimumState: Lifecycle.State) {
		when (minimumState) {
			Lifecycle.State.RESUMED -> {
				attachDisposableToResumePause { viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToResumePause { viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop { viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToStartStop { viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy { viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToCreateDestroy { viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			else                    -> throw RuntimeException("minimumState $minimumState not supported")
		}
	}

	private fun attachDisposableToCreateDestroy(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtCreateDetachAtDestroy(lifecycleOwner, createDisposable)
	}

	private fun attachDisposableToStartStop(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtStartDetachAtStop(lifecycleOwner, createDisposable)
	}

	private fun attachDisposableToResumePause(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtResumeDetachAtPause(lifecycleOwner, createDisposable)
	}

	override fun onDestroy(owner: LifecycleOwner) {
		clear()
	}

	fun addComponents(components: List<AttachedComponent>) {
		components.forEach {
			if (it is ViewModelAttacher) addViewModelAttacher(it)
			if (it is StateRenderer) addStateRenderer(it)
			if (it is SignalResponder) addSignalResponder(it)
		}
	}
}