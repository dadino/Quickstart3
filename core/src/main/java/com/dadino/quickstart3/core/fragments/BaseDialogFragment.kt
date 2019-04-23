package com.dadino.quickstart3.core.fragments

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dadino.quickstart3.core.components.AttachedComponent
import com.dadino.quickstart3.core.components.AttachedComponentController
import com.dadino.quickstart3.core.components.EventManager
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter

abstract class BaseDialogFragment : DialogFragment() {

	protected val eventManager: EventManager = EventManager()
	protected val components: AttachedComponentController by lazy {
		AttachedComponentController(lifecycleOwner = this,
				eventManager = eventManager,
				renderFun = { state -> renderState(state) },
				respondFun = { signal -> respondTo(signal) })
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		components.addComponents(components())
		components.attachViewModels(viewModels())
	}

	open fun components(): List<AttachedComponent> = listOf()
	open fun viewModels(): List<VMStarter> = listOf()
	open fun renderState(state: State) {}
	open fun respondTo(signal: Signal) {}
}