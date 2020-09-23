package com.dadino.quickstart3.sample

import androidx.multidex.MultiDexApplication
import com.dadino.quickstart3.sample.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SampleApp : MultiDexApplication() {

	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@SampleApp)
			modules(AppModule.module)
		}
	}
}