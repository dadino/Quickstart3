package com.dadino.quickstart3.contextformattable

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat

open class StringFormattable(
	private val text: String,
	private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return if (args.isNotEmpty()) String.format(text, *args) else text
	}
}

open class HtmlFormattable(
	private val htmlText: String,
	private val flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT
) : ContextFormattable {

	override fun format(context: Context): Spanned? {
		return HtmlCompat.fromHtml(htmlText, flags)
	}
}

fun String.asFormattable(): ContextFormattable = StringFormattable(this)