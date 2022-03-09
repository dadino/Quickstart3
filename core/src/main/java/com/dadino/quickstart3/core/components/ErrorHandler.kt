package com.dadino.quickstart3.core.components

interface ErrorHandler {
	fun getError(error: Throwable?): com.dadino.quickstart3.contextformattable.Error
}