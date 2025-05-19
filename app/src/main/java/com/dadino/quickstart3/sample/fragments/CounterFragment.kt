package com.dadino.quickstart3.sample.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.fragments.BaseFragment
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.sample.utils.ImageUriHelper
import com.dadino.quickstart3.sample.viewmodels.counter.CounterEvent
import com.dadino.quickstart3.sample.viewmodels.counter.CounterSignal
import com.dadino.quickstart3.sample.viewmodels.counter.CounterState
import com.dadino.quickstart3.sample.viewmodels.counter.CounterSubState
import com.dadino.quickstart3.sample.viewmodels.counter.CounterViewModel
import com.jakewharton.rxbinding3.view.clicks
import org.koin.android.viewmodel.ext.android.viewModel

class CounterFragment : BaseFragment() {

  private lateinit var counterButton: Button
  private lateinit var counterDelayedButton: Button
  private lateinit var counterStateButton: Button

  private val counterViewModel: CounterViewModel by viewModel()

  private var uriForNextPhoto: Uri? = null
  private val takePictureContract = registerForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken: Boolean ->
	uriForNextPhoto?.let {
	  if (pictureTaken) eventManager.receiveEvent(CounterEvent.OnAdvanceCounterClicked)
	  uriForNextPhoto = null
	}
  }

  override fun viewModels(): List<VMStarter> {
	return listOf(
	  VMStarter { counterViewModel }
	)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
	return inflater.inflate(R.layout.fragment_counter, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
	super.onViewCreated(view, savedInstanceState)
	counterButton = view.findViewById<Button>(R.id.example_data_counter)
	counterDelayedButton = view.findViewById<Button>(R.id.example_data_counter_delayed)
	counterStateButton = view.findViewById<Button>(R.id.example_data_counter_state)

	eventManager.attachEventSources(
	  mapOf(
		"counter" to counterButton.clicks().map { CounterEvent.OnAdvanceCounterClicked },
		"counterDelayed" to counterDelayedButton.clicks().map { CounterEvent.OnDelayedAdvanceCounterClicked },
		"counterState" to counterStateButton.clicks().map {
		  takePicture()
		  CounterEvent.OnShowCounterStateClicked
		},
	  )
	)
  }

  override fun respondTo(signal: Signal) {
	when (signal) {
	  is CounterSignal.ShowCounterState -> {
		Toast.makeText(requireContext(), "Counter: ${signal.counter}", Toast.LENGTH_SHORT).show()
	  }
	}
  }

  override fun renderState(state: State) {
	when (state) {
	  is CounterState -> {
		render(state)
	  }

	  is CounterSubState -> {
		render(state)
	  }
	}
  }

  private fun render(state: CounterState) {
	QuickLogger.tag("Counter").d { "State: $state" }
	counterButton?.text = state.counter.toString()
  }

  private fun render(state: CounterSubState) {
	QuickLogger.tag("CounterSub").d { "State: $state" }
  }

  private fun takePicture() {
	val uri: Uri = ImageUriHelper.createImageUri(requireContext())

	uriForNextPhoto = uri
	takePictureContract.launch(uri)
  }
}