package com.dadino.quickstart3.sample

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dadino.quickstart3.core.components.*
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.sample.viewmodels.counter.*
import org.koin.android.viewmodel.ext.android.viewModel

class CounterComponent(activity: AppCompatActivity, context: Context) : SignalResponder, StateRenderer, ViewModelAttacher {

	var counterButton: Button? = null
	private val appContext: Context = context.applicationContext
	private val counterViewModel: CounterViewModel by activity.viewModel()

	override fun attachAdditionalViewModels(): List<VMStarter> {
		return listOf(
			VMStarter { counterViewModel }
				.apply { queueEvents(listOf(CounterEvent.OnAdvanceCounterClicked, CounterEvent.OnAdvanceCounterClicked)) }
		)
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

	override fun renderState(state: State<*>): Boolean {
		return when (state) {
			is CounterState -> {
				render(state)
				true
			}
			is CounterSubState -> {
				render(state)
				true
			}
			else               -> false
		}
	}

	private fun render(state: CounterState) {
		Log.d("Counter", "State: $state")
		counterButton?.text = state.counter.toString()
	}

	private fun render(state: CounterSubState) {
		Log.d("CounterSub", "State: $state")
	}
}