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

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is StringFormattable) return false

	if (text != other.text) return false
	if (!args.contentEquals(other.args)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = text.hashCode()
	result = 31 * result + args.contentHashCode()
	return result
  }
}

open class HtmlFormattable(
	private val htmlText: String,
	private val flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT
) : ContextFormattable {

  override fun format(context: Context): Spanned? {
	return HtmlCompat.fromHtml(htmlText, flags)
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is HtmlFormattable) return false

	if (htmlText != other.htmlText) return false
	if (flags != other.flags) return false

	return true
  }

  override fun hashCode(): Int {
	var result = htmlText.hashCode()
	result = 31 * result + flags
	return result
  }
}

fun String.asFormattable(): ContextFormattable = StringFormattable(this)