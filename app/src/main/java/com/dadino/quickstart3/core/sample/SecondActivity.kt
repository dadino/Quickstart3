package com.dadino.quickstart3.core.sample

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dadino.quickstart3.core.BaseActivity
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.sample.entities.OnSaveSessionRequested
import com.dadino.quickstart3.core.sample.viewmodels.SpinnerState
import com.dadino.quickstart3.core.sample.viewmodels.SpinnerViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_second.*
import org.koin.android.architecture.ext.viewModel

class SecondActivity : BaseActivity() {

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
