package com.dadino.quickstart3.core.components

import androidx.lifecycle.*
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.utils.AttachDetachCallback
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
		list.forEach { attachViewModel(it) }
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

	private fun attachViewModel(vmStarter: VMStarter) {
		doAtLifecycle(vmStarter)
		attachToLifecycle(vmStarter)
	}

	private fun doAtLifecycle(vmStarter: VMStarter) {
		when (vmStarter.minimumState) {
			Lifecycle.State.RESUMED -> WorkerLifecycle.doAtResume(lifecycleOwner, {
				vmStarter.viewModel.attachEventSource(eventManager.interactionEvents())
				vmStarter.eventCallbacks?.onEventManagerAttached()
			})
			Lifecycle.State.STARTED -> WorkerLifecycle.doAtStart(lifecycleOwner, {
				vmStarter.viewModel.attachEventSource(eventManager.interactionEvents())
				vmStarter.eventCallbacks?.onEventManagerAttached()
			})
			Lifecycle.State.CREATED -> WorkerLifecycle.doAtCreate(lifecycleOwner, {
				vmStarter.viewModel.attachEventSource(eventManager.interactionEvents())
				vmStarter.eventCallbacks?.onEventManagerAttached()
			})
			else                    -> throw RuntimeException("minimumState ${vmStarter.minimumState} not supported")
		}
	}

	private fun attachToLifecycle(vmStarter: VMStarter) {
		when (vmStarter.minimumState) {
			Lifecycle.State.RESUMED -> {
				attachDisposableToResumePause(vmStarter.stateUpdatesCallbacks) { vmStarter.viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToResumePause(vmStarter.signalUpdatesCallbacks) { vmStarter.viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			Lifecycle.State.STARTED -> {
				attachDisposableToStartStop(vmStarter.stateUpdatesCallbacks) { vmStarter.viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToStartStop(vmStarter.signalUpdatesCallbacks) { vmStarter.viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			Lifecycle.State.CREATED -> {
				attachDisposableToCreateDestroy(vmStarter.stateUpdatesCallbacks) { vmStarter.viewModel.states().subscribeBy(onNext = { renderStateInternal(it) }) }
				attachDisposableToCreateDestroy(vmStarter.signalUpdatesCallbacks) { vmStarter.viewModel.signals().subscribeBy(onNext = { respondToInternal(it) }) }
			}
			else                    -> throw RuntimeException("minimumState ${vmStarter.minimumState} not supported")
		}
	}

	private fun attachDisposableToCreateDestroy(callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtCreateDetachAtDestroy(lifecycleOwner, callback, createDisposable)
	}

	private fun attachDisposableToStartStop(callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtStartDetachAtStop(lifecycleOwner, callback, createDisposable)
	}

	private fun attachDisposableToResumePause(callback: AttachDetachCallback?, createDisposable: () -> Disposable) {
		DisposableLifecycle.attachAtResumeDetachAtPause(lifecycleOwner, callback, createDisposable)
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