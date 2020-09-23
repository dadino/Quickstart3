package com.dadino.quickstart3.core.components

import android.content.Context
import androidx.annotation.StringRes

interface ContextFormattable {

	fun format(context: Context): String?
}

class ResFormattable(
		private @StringRes val textRes: Int,
		private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return context.getString(textRes, args)
	}
}

class StringFormattable(
		private val text: String,
		private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return String.format(text, args)
	}
}
