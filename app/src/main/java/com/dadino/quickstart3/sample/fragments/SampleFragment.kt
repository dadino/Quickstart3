package com.dadino.quickstart3.sample.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.fragments.BaseFragment
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerEvent
import com.jakewharton.rxbinding3.view.clicks

class SampleFragment : BaseFragment() {
	private lateinit var sampleButton: Button
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_sample, container, true)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sampleButton = view.findViewById(R.id.sample_button)

		eventManager.attachEventSource(sampleButton.clicks().map { SpinnerEvent.OnSpinnerIdleClicked() })
	}

	override fun renderState(state: State) {
	}

	override fun respondTo(signal: Signal) {
	}
}