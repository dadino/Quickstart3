package com.dadino.quickstart3.core.components

import android.content.Context
import android.text.Spanned
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat

interface ContextFormattable {

	fun format(context: Context): CharSequence?
}

class ResFormattable(
		@StringRes private val textRes: Int,
		private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return context.getString(textRes, *args)
	}
}

class StringFormattable(
		private val text: String,
		private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return String.format(text, *args)
	}
}

class HtmlFormattable(
		private val htmlText: String,
		private val flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT
) : ContextFormattable {

	override fun format(context: Context): Spanned? {
		return HtmlCompat.fromHtml(htmlText, flags)
	}
}
