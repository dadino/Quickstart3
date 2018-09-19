package com.dadino.quickstart3.core.entities


open class Signal(private val payload: SignalPayload) {
	private var consumed: Boolean = false

	fun doAndConsume(action: (SignalPayload) -> Unit) {
		if (consumed.not()) {
			action(payload)
			consumed = true
		}
	}

	override fun toString(): String {
		return "{value: $payload, consumed: $consumed}"
	}

	companion object {
		fun doAndConsume(signal: Signal?, action: (SignalPayload) -> Unit) {
			signal?.let { s -> s.doAndConsume { action(it) } }
		}
	}
}
