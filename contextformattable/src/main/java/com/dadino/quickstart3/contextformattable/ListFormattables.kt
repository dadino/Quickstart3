package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.core.text.HtmlCompat
import kotlinx.parcelize.Parcelize

@Parcelize
open class MapFormattable(val map: Map<ContextFormattable, ContextFormattable?>) : ContextFormattable {

  override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
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
	  val text = sb.toString()
	  if (modifiers.contains(RawHtmlModifier))
		text else
		HtmlCompat.fromHtml(
		  text,
		  HtmlCompat.FROM_HTML_MODE_COMPACT
			  or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST
			  or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
			  or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
		)
	} else null
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

@Parcelize
open class ListFormattable(private val separator: ContextFormattable?, private vararg val items: ContextFormattable?) : ContextFormattable {

  override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
	val mapNotNull = items.toList().mapNotNull { it }
	return if (mapNotNull.isEmpty()) null else mapNotNull.joinToString(separator = separator?.format(context) ?: "") { it.format(context) ?: "" }
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is ListFormattable) return false

	if (separator != other.separator) return false
	if (!items.contentEquals(other.items)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = separator?.hashCode() ?: 0
	result = 31 * result + items.contentHashCode()
	return result
  }
}

@Parcelize
class DotListFormattable(private vararg val items: ContextFormattable?) : ListFormattable(separator = " • ".asFormattable(), *items)