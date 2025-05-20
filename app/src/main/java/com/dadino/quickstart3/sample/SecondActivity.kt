package com.dadino.quickstart3.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerEvent
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerState
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding3.view.clicks
import org.koin.android.viewmodel.ext.android.viewModel

class SecondActivity : BaseActivity() {

  private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
  private val fab: FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }

  private val spinnerViewModel: SpinnerViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
	super.onCreate(savedInstanceState)
	setContentView(R.layout.activity_second)
	QuickLogger.tag("Second").d { "onCreate" }
	setSupportActionBar(toolbar)

	supportActionBar?.setDisplayHomeAsUpEnabled(true)

	eventManager.attachEventSource("fab", fab.clicks().map {
	  SpinnerEvent.OnSaveSessionRequested("Second")
	})
  }

  override fun onStart() {
	super.onStart()

	QuickLogger.tag("Second").d { "onStart" }
  }

  override fun onResume() {
	super.onResume()
	QuickLogger.tag("Second").d { "onResume" }
  }

  override fun viewModels(): List<VMStarter> {
	return listOf(
	  VMStarter { spinnerViewModel }
	)
  }

  override fun respondTo(signal: Signal) {
	when (signal) {
	  is SpinnerSignal.ShowSaveSessionCompleted -> Toast.makeText(this, "Session saved", Toast.LENGTH_LONG).show()
	}
  }

  override fun renderState(state: State) {
	when (state) {
	  is SpinnerState -> render(state)
	}
  }

  private fun render(state: SpinnerState) {
	QuickLogger.tag("Second").d { "State: $state" }

	Toast.makeText(this, "Session: ${spinnerViewModel.currentState().session}", Toast.LENGTH_LONG).show()
  }
}
