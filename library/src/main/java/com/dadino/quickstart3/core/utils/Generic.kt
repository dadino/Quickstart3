package com.dadino.quickstart3.core.utils


class Generic<T : Any>(val klass: Class<T>) {
	companion object {
		inline operator fun <reified T : Any> invoke() = Generic(T::class.java)
	}

	fun isCorrectType(t: Any): Boolean {
		return when {
			klass.isAssignableFrom(t.javaClass) -> true
			else                                -> false
		}

	}
}