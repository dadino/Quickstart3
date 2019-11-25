package com.dadino.quickstart3.core

import org.mockito.Mockito


class Any<T> {

	fun any(): T {
		Mockito.any<T>()
		return uninitialized()
	}

	private fun uninitialized(): T = null as T
}