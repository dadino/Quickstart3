package com.dadino.quickstart3.core.entities

import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.utils.AttachDetachCallback

data class VMStarter(
		val eventCallbacks: EventCallbacks? = null,
		val stateUpdatesCallbacks: AttachDetachCallback? = null,
		val signalUpdatesCallbacks: AttachDetachCallback? = null,
		val minimumState: Lifecycle.State = Lifecycle.State.RESUMED,
		val events: (() -> List<Event>)? = null,
		private val viewModelFactory: () -> BaseViewModel<*>
) {

	val viewModel: BaseViewModel<*> by lazy {
		viewModelFactory().apply {
			actionsToPerformOnConnect = listOf(
				{ eventCallbacks?.onEventManagerAttached() },
				{
					events?.let {
						it().forEach { receiveEvent(it) }
					}
				})
		}
	}
}

interface EventCallbacks {

	fun onEventManagerAttached()
}