package com.dadino.quickstart3.core.entities

import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.OnConnectCallback
import java.util.*

data class VMStarter(
		val minimumState: Lifecycle.State = Lifecycle.State.RESUMED,
		private val viewModelFactory: () -> BaseViewModel<*>
) {

	private val eventQueue: Queue<Event> = LinkedList<Event>()

	private val viewModelDelegate = lazy {
		viewModelFactory().apply {
			onConnectCallback = object : OnConnectCallback {
				override fun onConnect() {
					dequeueEvents()
				}
			}

			if (canReceiveEvents()) dequeueEvents()
		}
	}

	val viewModel: BaseViewModel<*> by viewModelDelegate

	fun queueEvent(event: Event) {
		queueEvents(listOf(event))
	}

	fun queueEvents(events: List<Event>) {
		events.forEach {
			eventQueue.offer(it)
		}
		if (viewModelDelegate.isInitialized() && viewModel.canReceiveEvents()) dequeueEvents()
	}

	private fun dequeueEvents() {
		var hasNext = true
		while (hasNext) {
			val event = eventQueue.poll()
			if (event != null) {
				viewModel.receiveEvent(event)
				hasNext = true
			} else {
				hasNext = false
			}
		}
	}
}