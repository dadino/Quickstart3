package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.HtmlCompat
import com.aghajari.compose.text.ContentAnnotatedString
import com.aghajari.compose.text.fromHtml
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

abstract class BuildableHtmlFormattable() : ContextFormattable {
  abstract fun getHtmlText(context: Context): String?
  open fun getFlags(): Int = HtmlCompat.FROM_HTML_MODE_COMPACT

  override fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence? {
	val htmlText = getHtmlText(context) ?: return null
	return if (modifiers.contains(RawHtmlModifier)) htmlText
	else
	  HtmlCompat.fromHtml(htmlText, getFlags())
  }

  @Composable
  override fun asAnnotatedString(modifiers: ImmutableList<CFModifier>): ContentAnnotatedString? {
	val context = LocalContext.current
	val htmlText = remember(this, modifiers) { getHtmlText(context) }
	val flags = remember(this) { getFlags() }
	return remember(htmlText, flags) { htmlText?.fromHtml(flags) }
  }
}

@Parcelize
open class HtmlFormattable(
  private val htmlText: String,
  private val flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT
) : BuildableHtmlFormattable() {
  override fun getHtmlText(context: Context): String? {
	return htmlText
  }

  override fun getFlags(): Int {
	return HtmlCompat.FROM_HTML_MODE_COMPACT
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

fun String.asHtmlFormattable(): ContextFormattable = HtmlFormattable(this)