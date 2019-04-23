package com.dadino.quickstart3.core.entities

import androidx.lifecycle.Lifecycle
import com.dadino.quickstart3.core.components.BaseViewModel


data class VMStarter(val viewModel: BaseViewModel<*>,
					 val minimumState: Lifecycle.State = Lifecycle.State.RESUMED)