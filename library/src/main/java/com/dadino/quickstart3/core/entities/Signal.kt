package com.dadino.quickstart3.core.entities

class Signal {
	private var consumed: Boolean = false

	fun doAndConsume(action: () -> Unit) {
		if (consumed.not()) {
			action()
			consumed = true
		}
	}

	override fun toString(): String {
		return "{consumed: $consumed}"
	}

	companion object {
		fun doAndConsume(signal: Signal?, action: () -> Unit) {
			signal?.let { it.doAndConsume { action() } }
		}
	}
}

open class SignalWithValue<out T>(private val value: T) {
	private var consumed: Boolean = false

	fun doAndConsume(action: (T) -> Unit) {
		if (consumed.not()) {
			action(value)
			consumed = true
		}
	}

	override fun toString(): String {
		return "{value: $value, consumed: $consumed}"
	}

	companion object {
		fun <T> doAndConsume(signal: SignalWithValue<T>?, action: (T) -> Unit) {
			signal?.let { it.doAndConsume { action(it) } }
		}
	}
}

class ErrorSignal(error: Throwable) : SignalWithValue<Throwable>(error) {
	companion object {
		fun showError(signal: ErrorSignal?, action: (Throwable) -> Unit) {
			signal?.let { it.doAndConsume { action(it) } }
		}
	}
}

