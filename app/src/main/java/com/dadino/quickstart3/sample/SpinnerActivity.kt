package com.dadino.quickstart3.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.NoOpEvent
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.components.EventTransformer
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerEvent
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSaveState
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerState
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerViewModel
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

  private val spinnerViewModel: SpinnerViewModel by viewModel()

  private val spinnerVMStarter: VMStarter by lazy { VMStarter { spinnerViewModel } }

  override fun onCreate(savedInstanceState: Bundle?) {
	super.onCreate(savedInstanceState)
	setContentView(R.layout.activity_spinner)
	QuickLogger.plant(QuickLogger.DebugTree())
	QuickLogger.tag("Spinner").d { "onCreate" }

	eventManager.attachEventSource(
	  "ui", Observable.merge(
		listOf(
		  idle.clicks().map { SpinnerEvent.OnSpinnerIdleClicked() },
		  loading.clicks().map { SpinnerEvent.OnSpinnerLoadingClicked() },
		  error.clicks().map { SpinnerEvent.OnSpinnerErrorClicked() },
		  done.clicks().map { SpinnerEvent.OnSpinnerDoneClicked() },
		  secondPage.clicks().map {
			openGridActivity()
			NoOpEvent
		  },
		  saveSession.clicks().map { SpinnerEvent.OnSaveSessionRequested("First") },
		  spinner.interactionEvents()
		)
	  )
	)
	eventManager.eventTransformer = SpinnerTransformer()
	eventManager.tag = "SpinnerEventManager"
  }

  override fun onStart() {
	super.onStart()

	QuickLogger.tag("Spinner").d { "onStart" }
  }

  override fun onResume() {
	super.onResume()
	QuickLogger.tag("Spinner").d { "onResume" }
	spinnerVMStarter.queueEvent(SpinnerEvent.OnSpinnerLoadingClicked())
  }

  override fun viewModels(): List<VMStarter> {
	return listOf(
	  spinnerVMStarter
	)
  }

  override fun renderState(state: State) {
	when (state) {
	  is SpinnerState     -> render(state)
	  is SpinnerSaveState -> render(state)
	}
  }

  override fun respondTo(signal: Signal) {
	when (signal) {
	  is SpinnerSignal.ShowDoneToast            -> Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
	  is SpinnerSignal.ShowSaveSessionCompleted -> Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show()
	  is SpinnerSignal.ShowLoadSessionCompleted -> Toast.makeText(this, "Session loaded: ${signal.session}", Toast.LENGTH_SHORT).show()
	  is SpinnerSignal.OpenSecondActivity       -> openGridActivity()
	}
  }

  private fun openGridActivity() {
	startActivity(Intent(this, GridActivity::class.java))
  }

  private fun render(state: SpinnerState) {
	QuickLogger.tag("Spinner").d { "State: $state" }
	spinner.setState(state.list, state.loading, state.error)
	spinner.selectedId = state.selectedId ?: -1
  }

  private fun render(state: SpinnerSaveState) {
	QuickLogger.tag("SpinnerSave").d { "State: $state" }
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