package com.dadino.quickstart3.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dadino.quickstart3.core.components.*
import com.dadino.quickstart3.core.entities.*

abstract class BaseActivity : AppCompatActivity() {

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
	open fun renderState(state: State<*>) {}
	open fun respondTo(signal: Signal) {}
}