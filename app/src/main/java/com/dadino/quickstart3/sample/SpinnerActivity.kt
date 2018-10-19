package com.dadino.quickstart3.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.sample.entities.OnGoToSecondPageClicked
import com.dadino.quickstart3.sample.viewmodels.counter.CounterEvent
import com.dadino.quickstart3.sample.viewmodels.counter.CounterSignal
import com.dadino.quickstart3.sample.viewmodels.counter.CounterState
import com.dadino.quickstart3.sample.viewmodels.counter.CounterViewModel
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerEvent
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerState
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerViewModel
import com.dadino.quickstart3.sample.widgets.ExampleSpinner
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import org.koin.android.architecture.ext.viewModel

class SpinnerActivity : BaseActivity() {

	private val spinner: ExampleSpinner by lazy { findViewById<ExampleSpinner>(R.id.example_data_spinner) }
	private val idle: Button by lazy { findViewById<Button>(R.id.example_data_idle) }
	private val loading: Button by lazy { findViewById<Button>(R.id.example_data_loading) }
	private val error: Button by lazy { findViewById<Button>(R.id.example_data_error) }
	private val done: Button by lazy { findViewById<Button>(R.id.example_data_done) }
	private val secondPage: Button by lazy { findViewById<Button>(R.id.example_data_go_to_second_page) }
	private val saveSession: Button by lazy { findViewById<Button>(R.id.example_data_save_session) }
	private val counterButton: Button by lazy { findViewById<Button>(R.id.example_data_counter) }
	private val counterStateButton: Button by lazy { findViewById<Button>(R.id.example_data_counter_state) }

	private val spinnerViewModel: SpinnerViewModel by viewModel()
	private val counterViewModel: CounterViewModel by viewModel()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_spinner)
		spinner.setOnRetryClickListener(View.OnClickListener { eventManager.receiveInteractionEvent(SpinnerEvent.OnSpinnerRetryClicked()) })

		attachViewModel(spinnerViewModel)
		attachViewModel(counterViewModel, Lifecycle.State.RESUMED)
		counterViewModel.receiveEvent(CounterEvent.SetCounter(100))

		eventManager.eventCollection = Observable.merge(listOf(
				idle.clicks().map { SpinnerEvent.OnSpinnerIdleClicked() },
				loading.clicks().map { SpinnerEvent.OnSpinnerLoadingClicked() },
				error.clicks().map { SpinnerEvent.OnSpinnerErrorClicked() },
				done.clicks().map { SpinnerEvent.OnSpinnerDoneClicked() },
				secondPage.clicks().map { OnGoToSecondPageClicked() },
				saveSession.clicks().map { SpinnerEvent.OnSaveSessionRequested("First") },
				counterButton.clicks().map { CounterEvent.OnAdvanceCounterClicked },
				counterStateButton.clicks().map { CounterEvent.OnShowCounterStateClicked },
				spinner.interactionEvents()
		))
	}


	override fun renderState(state: State) {
		when (state) {
			is SpinnerState -> render(state)
			is CounterState -> render(state)
		}
	}

	override fun respondTo(signal: Signal) {
		when (signal) {
			is SpinnerSignal.ShowDoneToast            -> Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.ShowSaveSessionCompleted -> Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.ShowLoadSessionCompleted -> Toast.makeText(this, "Session loaded: ${signal.session}", Toast.LENGTH_SHORT).show()
			is CounterSignal.ShowCounterState         -> Toast.makeText(this, "Counter: ${signal.counter}", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.OpenSecondActivity       -> startActivity(Intent(this, SecondActivity::class.java))
		}
	}

	private fun render(state: SpinnerState) {
		Log.d("Spinner", "State: $state")
		spinner.setState(state.list, state.loading, state.error)
		spinner.selectedId = state.selectedId ?: -1
	}

	private fun render(state: CounterState) {
		Log.d("Counter", "State: $state")
		counterButton.text = state.counter.toString()
	}
}