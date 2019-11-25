package com.dadino.quickstart3.core


import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


class RxImmediateSchedulerRule : TestRule {


	override fun apply(base: Statement, description: Description): Statement {
		return object : Statement() {
			@Throws(Throwable::class)
			override fun evaluate() {
				RxJavaSchedulerConfigurator.prepareRxJava()

				try {
					base.evaluate()
				} finally {
					RxJavaPlugins.reset()
					RxAndroidPlugins.reset()
				}
			}
		}
	}

}

object RxJavaSchedulerConfigurator {
	private val trampoline = Schedulers.trampoline()

	fun prepareRxJava() {
		RxJavaPlugins.setInitIoSchedulerHandler { trampoline }
		RxJavaPlugins.setInitComputationSchedulerHandler { trampoline }
		RxJavaPlugins.setInitNewThreadSchedulerHandler { trampoline }
		RxJavaPlugins.setInitSingleSchedulerHandler { trampoline }
		RxAndroidPlugins.setInitMainThreadSchedulerHandler { trampoline }
	}
}