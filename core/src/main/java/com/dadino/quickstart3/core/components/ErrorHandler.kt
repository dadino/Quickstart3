package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Error

interface ErrorHandler {
	fun getError(error: Throwable?): Error
}