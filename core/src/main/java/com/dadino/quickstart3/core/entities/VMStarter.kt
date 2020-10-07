package com.dadino.quickstart3.core.entities

import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.utils.AttachDetachCallback

data class VMStarter(
		val eventCallbacks: EventCallbacks? = null,
		val stateUpdatesCallbacks: AttachDetachCallback? = null,
		val signalUpdatesCallbacks: AttachDetachCallback? = null,
		val minimumState: Lifecycle.State = Lifecycle.State.RESUMED,
		private val viewModelFactory: () -> BaseViewModel<*>
) {

	val viewModel: BaseViewModel<*> by lazy { viewModelFactory() }
}

interface EventCallbacks {

	fun onEventManagerAttached()
}