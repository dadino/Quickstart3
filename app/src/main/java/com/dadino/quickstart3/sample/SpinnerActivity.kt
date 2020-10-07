package com.dadino.quickstart3.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.components.AttachedComponent
import com.dadino.quickstart3.core.components.EventTransformer
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.sample.entities.OnGoToSecondPageClicked
import com.dadino.quickstart3.sample.viewmodels.counter.CounterEvent
import com.dadino.quickstart3.sample.viewmodels.spinner.*
import com.dadino.quickstart3.sample.widgets.ExampleSpinner
import com.dadino.quickstart3.ui.widgets.LoadingSpinnerEvent
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.viewModel

class SpinnerActivity : BaseActivity() {

	private val spinner: ExampleSpinner by lazy { findViewById<ExampleSpinner>(R.id.example_data_spinner) }
	private val idle: Button by lazy { findViewById<Button>(R.id.example_data_idle) }
	private val loading: Button by lazy { findViewById<Button>(R.id.example_data_loading) }
	private val error: Button by lazy { findViewById<Button>(R.id.example_data_error) }
	private val done: Button by lazy { findViewById<Button>(R.id.example_data_done) }
	private val secondPage: Button by lazy { findViewById<Button>(R.id.example_data_go_to_second_page) }
	private val saveSession: Button by lazy { findViewById<Button>(R.id.example_data_save_session) }
	private val counterButton: Button by lazy { findViewById<Button>(R.id.example_data_counter) }
	private val counterDelayedButton: Button by lazy { findViewById<Button>(R.id.example_data_counter_delayed) }
	private val counterStateButton: Button by lazy { findViewById<Button>(R.id.example_data_counter_state) }

	private val spinnerViewModel: SpinnerViewModel by viewModel()
	private val counterComponent: CounterComponent by lazy { CounterComponent(this, this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_spinner)
		Log.d("Spinner", "onCreate")
		counterComponent.counterButton = counterButton

		eventManager.attachEventSource(
			Observable.merge(
				listOf(
					idle.clicks().map { SpinnerEvent.OnSpinnerIdleClicked() },
					loading.clicks().map { SpinnerEvent.OnSpinnerLoadingClicked() },
					error.clicks().map { SpinnerEvent.OnSpinnerErrorClicked() },
					done.clicks().map { SpinnerEvent.OnSpinnerDoneClicked() },
					secondPage.clicks().map { OnGoToSecondPageClicked() },
					saveSession.clicks().map { SpinnerEvent.OnSaveSessionRequested("First") },
					counterButton.clicks().map { CounterEvent.OnAdvanceCounterClicked },
					counterDelayedButton.clicks().map { CounterEvent.OnDelayedAdvanceCounterClicked },
					counterStateButton.clicks().map { CounterEvent.OnShowCounterStateClicked },
					spinner.interactionEvents()
				)
			)
		)
		eventManager.eventTransformer = SpinnerTransformer()
		eventManager.tag = "SpinnerEventManager"

		//eventManager.receiveEvent(CounterEvent.SetCounter(100))
	}

	override fun onStart() {
		super.onStart()

		Log.d("Spinner", "onStart")
	}

	override fun onResume() {
		super.onResume()
		Log.d("Spinner", "onResume")
	}

	override fun components(): List<AttachedComponent> {
		return listOf(
			counterComponent
		)
	}

	override fun viewModels(): List<VMStarter> {
		return listOf(
			VMStarter { spinnerViewModel }
		)
	}

	override fun renderState(state: State) {
		when (state) {
			is SpinnerState -> render(state)
		}
	}

	override fun respondTo(signal: Signal) {
		when (signal) {
			is SpinnerSignal.ShowDoneToast -> Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.ShowSaveSessionCompleted -> Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.ShowLoadSessionCompleted -> Toast.makeText(this, "Session loaded: ${signal.session}", Toast.LENGTH_SHORT).show()
			is SpinnerSignal.OpenSecondActivity -> startActivity(Intent(this, SecondActivity::class.java))
		}
	}

	private fun render(state: SpinnerState) {
		Log.d("Spinner", "State: $state")
		spinner.setState(state.list, state.loading, state.error)
		spinner.selectedId = state.selectedId ?: -1
	}
}

class SpinnerTransformer : EventTransformer(true) {

	override fun transform(event: Event): Event? {
		return when (event) {
			is LoadingSpinnerEvent.OnRetryClicked -> SpinnerEvent.OnSpinnerRetryClicked()
			else                                  -> null
		}
	}
}