package com.dadino.quickstart3.core.components


interface ErrorHandler {
	fun formatError(error: Throwable?): String
	fun getCustomErrorCode(error: Throwable?): Int
}