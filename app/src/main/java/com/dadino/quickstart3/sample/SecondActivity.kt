package com.dadino.quickstart3.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.components.AttachedComponent
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerEvent
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerSignal
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerState
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding3.view.clicks
import org.koin.android.architecture.ext.viewModel

class SecondActivity : BaseActivity() {
	private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
	private val fab: FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }

	private val spinnerViewModel: SpinnerViewModel by viewModel()
	private val counterComponent: CounterComponent by lazy { CounterComponent(this, this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_second)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		eventManager.attachEventSource(fab.clicks().map {
			SpinnerEvent.OnSaveSessionRequested("Second")
		})

		//supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SampleFragment()).commit()
	}

	override fun components(): List<AttachedComponent> {
		return listOf(
				counterComponent
		)
	}

	override fun viewModels(): List<VMStarter> {
		return listOf(
				VMStarter(spinnerViewModel, Lifecycle.State.RESUMED)
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
		Log.d("Second", "State: $state")

		Toast.makeText(this, "Session: ${spinnerViewModel.currentState().session}", Toast.LENGTH_LONG).show()
	}

}
