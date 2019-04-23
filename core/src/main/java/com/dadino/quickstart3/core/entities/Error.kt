package com.dadino.quickstart3.core.entities

import android.content.Context
import androidx.annotation.StringRes


abstract class Error(val error: Throwable?) {
	open class WithStringRes(error: Throwable?, @StringRes private val messageResId: Int) : Error(error) {
		override fun format(context: Context): String = context.getString(messageResId)
	}

	open class WithMessage(error: Throwable?, private val message: String) : Error(error) {
		override fun format(context: Context): String = message
	}

	open class WithStringResAndMessage(error: Throwable?, @StringRes private val messageResId: Int, private val message: String) : Error(error) {
		override fun format(context: Context): String = context.getString(messageResId, message)
	}

	abstract fun format(context: Context): String
}


