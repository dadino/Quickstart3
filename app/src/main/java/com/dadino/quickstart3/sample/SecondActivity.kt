package com.dadino.quickstart3.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.sample.entities.OnSaveSessionRequested
import com.dadino.quickstart3.sample.viewmodels.SpinnerState
import com.dadino.quickstart3.sample.viewmodels.SpinnerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import org.koin.android.architecture.ext.viewModel

class SecondActivity : BaseActivity() {
	private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
	private val fab: FloatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.fab) }

	private val spinnerViewModel: SpinnerViewModel by viewModel()

	override fun initViews() {
		setContentView(R.layout.activity_second)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		attachViewModel(spinnerViewModel, Lifecycle.State.RESUMED) {
			render(it)
		}
	}

	override fun collectUserActions(): Observable<UserAction> {
		return fab.clicks().map {
			OnSaveSessionRequested("Second")
		}
	}

	private fun render(state: SpinnerState) {
		Log.d("Second", "State: $state")

		Toast.makeText(this, "Session: ${spinnerViewModel.state().session}", Toast.LENGTH_LONG).show()
	}

}
