package com.dadino.quickstart3.sample.di

import com.dadino.quickstart3.sample.repositories.ISessionRepository
import com.dadino.quickstart3.sample.repositories.MemorySessionRepository
import com.dadino.quickstart3.sample.viewmodels.CounterViewModel
import com.dadino.quickstart3.sample.viewmodels.SpinnerViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext


object AppModule {
	val module: Module = applicationContext {
		bean { MemorySessionRepository() as ISessionRepository }
		viewModel { SpinnerViewModel(get()) }
		viewModel { CounterViewModel() }
	}
}
