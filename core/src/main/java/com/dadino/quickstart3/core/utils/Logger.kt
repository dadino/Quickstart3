package com.dadino.quickstart3.core.utils

import android.util.Log


interface ILogger {
	fun log(tag: String, message: String)
}

class LogcatLogger : ILogger {
	override fun log(tag: String, message: String) {
		Log.d(tag, message)
	}
}

class ConsoleLogger : ILogger {
	override fun log(tag: String, message: String) {
		println("$tag: $message")
	}
}