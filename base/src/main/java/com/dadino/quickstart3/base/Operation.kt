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
}
