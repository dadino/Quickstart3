package com.dadino.quickstart3.core

import org.mockito.Mockito


object TestUtils {
	fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
	const val MAC_WAIT_TIME = 5000L

}