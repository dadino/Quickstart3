package com.dadino.quickstart3.sample.di

import com.dadino.quickstart3.sample.repositories.ISessionRepository
import com.dadino.quickstart3.sample.repositories.MemorySessionRepository
import com.dadino.quickstart3.sample.viewmodels.counter.CounterViewModel
import com.dadino.quickstart3.sample.viewmodels.grid.GridViewModel
import com.dadino.quickstart3.sample.viewmodels.spinner.SpinnerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


object AppModule {
	val module: Module = module {
		single { MemorySessionRepository() as ISessionRepository }
		viewModel { SpinnerViewModel(get()) }
		viewModel { CounterViewModel() }
		viewModel { GridViewModel() }
	}
}
