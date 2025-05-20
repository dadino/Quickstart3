package com.dadino.quickstart3.core.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.dadino.quickstart3.core.components.AttachedComponent
import com.dadino.quickstart3.core.components.AttachedComponentController
import com.dadino.quickstart3.core.components.EventManager
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.utils.QuickLogger

abstract class BaseFragment : Fragment() {

  protected val backCallback: OnBackPressedCallback = object : OnBackPressedCallback(false) {
	override fun handleOnBackPressed() {
	  doOnBackPress()
	}
  }
  protected val eventManager: EventManager = EventManager()
  protected val components: AttachedComponentController by lazy {
	AttachedComponentController(
	  lifecycleOwner = this,
	  eventManager = eventManager,
	  renderFun = { state -> renderState(state) },
	  respondFun = { signal -> respondTo(signal) })
  }

  override fun onCreate(savedInstanceState: Bundle?) {
	super.onCreate(savedInstanceState)
	components.addComponents(components())
	components.attachViewModels(viewModels())
	QuickLogger.tag(this::class.simpleName).d { "onCreate" }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
	super.onViewCreated(view, savedInstanceState)
	QuickLogger.tag(this::class.simpleName).d { "onViewCreated" }
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