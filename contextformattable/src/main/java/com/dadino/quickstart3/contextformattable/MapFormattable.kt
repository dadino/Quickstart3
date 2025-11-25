package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.text.HtmlCompat
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
open class MapFormattable(val map: Map<ContextFormattable, ContextFormattable?>) : BuildableHtmlFormattable() {
  override fun getHtmlText(context: Context): String? {
	if (map.isEmpty()) return null
	return if (map.any { it.value.isNullOrEmpty(context).not() }) {
	  val sb = StringBuilder()
	  map.entries
		.filter { it.value != null }
		.forEachIndexed { index, entry ->
		  val value = entry.value
		  val charSequence = value?.format(context, RawHtmlModifier)
		  if (charSequence != null) {
			if (index != 0) sb.append("<br>")
			sb.append("<b>${entry.key.format(context, RawHtmlModifier)}</b>: $charSequence")
		  }
		}
	  return sb.toString()
	} else null
  }

  override fun getFlags(): Int {
	return HtmlCompat.FROM_HTML_MODE_COMPACT or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is MapFormattable) return false

	if (map != other.map) return false

	return true
  }

  override fun hashCode(): Int {
	return map.hashCode()
  }
}