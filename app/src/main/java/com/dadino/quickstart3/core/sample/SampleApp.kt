package com.dadino.quickstart3.core.sample

import android.app.Application
import com.dadino.quickstart3.core.sample.di.AppModule
import org.koin.android.ext.android.startKoin


class SampleApp : Application() {
	override fun onCreate() {
		super.onCreate()
		startKoin(this, listOf(AppModule.module))
	}


}