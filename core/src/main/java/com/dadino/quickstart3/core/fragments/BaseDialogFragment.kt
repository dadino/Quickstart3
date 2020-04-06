package com.dadino.quickstart3.core.fragments

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import com.dadino.quickstart3.core.components.*
import com.dadino.quickstart3.core.entities.*

abstract class BaseDialogFragment : DialogFragment() {

	protected val backCallback: OnBackPressedCallback = object : OnBackPressedCallback(false) {
		override fun handleOnBackPressed() {
			doOnBackPress()
		}
	}
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

	override fun onAttach(context: Context) {
		super.onAttach(context)
		requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
	}

	open fun components(): List<AttachedComponent> = listOf()
	open fun viewModels(): List<VMStarter> = listOf()
	open fun renderState(state: State) {}
	open fun respondTo(signal: Signal) {}
	open fun doOnBackPress() {}
}