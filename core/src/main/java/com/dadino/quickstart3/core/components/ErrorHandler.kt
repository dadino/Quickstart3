package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Error


interface ErrorHandler {
	fun getError(error: Throwable?): Error
}