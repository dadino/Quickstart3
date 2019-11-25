package com.dadino.quickstart3.core

import org.mockito.Mockito


object TestUtils {
	fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
	const val MAX_WAIT_TIME_FOR_OBSERVABLES = 5000L

}