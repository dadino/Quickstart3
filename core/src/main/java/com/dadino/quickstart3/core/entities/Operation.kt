package com.dadino.quickstart3.core.entities

sealed class Operation {

	class Idle : Operation() {
		override fun toString(): String {
			return "Operation.IDLE"
		}
	}

	class InProgress : Operation() {
		override fun toString(): String {
			return "Operation.IN_PROGRESS"
		}
	}

	class Done : Operation() {
		override fun toString(): String {
			return "Operation.DONE"
		}
	}

	class Error(val error: Throwable) : Operation() {
		override fun toString(): String {
			return "Operation.ERROR {$error}"
		}
	}
}
