package com.dadino.quickstart3.core.entities

import android.util.Log
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel

data class VMStarter(
		val minimumState: Lifecycle.State = Lifecycle.State.RESUMED,
		private val viewModelFactory: () -> BaseViewModel<*>
) {

	val viewModel: BaseViewModel<*> by lazy {
		Log.d("VMStarter", "Generating view model with minimumState: $minimumState")
		viewModelFactory()
	}
}