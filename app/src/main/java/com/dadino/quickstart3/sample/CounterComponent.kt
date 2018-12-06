package com.dadino.quickstart3.sample

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.dadino.quickstart3.core.components.SignalResponder
import com.dadino.quickstart3.core.components.StateRenderer
import com.dadino.quickstart3.core.components.ViewModelAttacher
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.sample.viewmodels.counter.CounterSignal
import com.dadino.quickstart3.sample.viewmodels.counter.CounterState
import com.dadino.quickstart3.sample.viewmodels.counter.CounterViewModel
import org.koin.android.architecture.ext.viewModel


class CounterComponent(lifecycleOwner: LifecycleOwner, context: Context) : SignalResponder, StateRenderer, ViewModelAttacher {
	var counterButton: Button? = null
	private val appContext: Context = context.applicationContext
	private val counterViewModel: CounterViewModel by lifecycleOwner.viewModel()

	override fun attachAdditionalViewModels(): List<VMStarter> {
		return listOf(VMStarter(counterViewModel, Lifecycle.State.RESUMED))
	}

	override fun respondTo(signal: Signal): Boolean {
		return when (signal) {
			is CounterSignal.ShowCounterState -> {
				Toast.makeText(appContext, "Counter: ${signal.counter}", Toast.LENGTH_SHORT).show()
				true
			}
			else                              -> false
		}
	}

	override fun renderState(state: State): Boolean {
		return when (state) {
			is CounterState -> {
				render(state)
				true
			}
			else            -> false
		}
	}

	private fun render(state: CounterState) {
		Log.d("Counter", "State: $state")
		counterButton?.text = state.counter.toString()
	}
}