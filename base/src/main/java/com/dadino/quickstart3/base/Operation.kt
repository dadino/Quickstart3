package com.dadino.quickstart3.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Operation : Parcelable {

  object Idle : Operation() {
	override fun toString(): String {
	  return "Operation.IDLE"
	}
  }

  object InProgress : Operation() {
	override fun toString(): String {
	  return "Operation.IN_PROGRESS"
	}
  }

  object Done : Operation() {
	override fun toString(): String {
	  return "Operation.DONE"
	}
  }

  class Error(val error: Throwable) : Operation() {
	override fun toString(): String {
	  return "Operation.ERROR {$error}"
	}
  }

  companion object {
	fun idle(): Operation = Idle
	fun inProgress(): Operation = InProgress
	fun done(): Operation = Done
	fun error(error: Throwable): Operation = Error(error)

	fun combine(operations: List<Operation>): Operation {
	  return if (operations.any { it is Error }) {
		val errorOperations = operations.filterIsInstance<Error>()
		if (errorOperations.size == 1) error(errorOperations.first().error)
		else error(CompositeException(errorOperations.map { it.error }))
	  } else if (operations.any { it is InProgress }) {
		inProgress()
	  } else if (operations.all { it is Done }) {
		done()
	  } else idle()
	}
  }
}

fun Operation.combine(vararg other: Operation): Operation = Operation.combine(listOf(this) + other.toList())

class CompositeException(val throwables: List<Throwable>) : Throwable()

