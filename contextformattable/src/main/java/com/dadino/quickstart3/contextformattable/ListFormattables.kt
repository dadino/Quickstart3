package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.core.text.HtmlCompat

open class MapFormattable(val map: Map<ContextFormattable, ContextFormattable?>) : ContextFormattable {

	override fun format(context: Context): CharSequence? {
		if (map.isEmpty()) return null
		return if (map.any { it.value.isNullOrEmpty(context).not() }) {
			val sb = StringBuilder()
			map.entries
				.filter { it.value != null }
				.forEachIndexed { index, entry ->
					val value = entry.value
					if (value != null) {
						if (index != 0) sb.append("<br>")
						sb.append("<b>${entry.key.format(context)}</b>: ${value.format(context)}")
					}
				}
			HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT)
		} else null
	}
}

open class ListFormattable(private val separator: ContextFormattable?, private vararg val items: ContextFormattable?) : ContextFormattable {

	override fun format(context: Context): CharSequence? {
		val mapNotNull = items.toList().mapNotNull { it }
		return if (mapNotNull.isEmpty()) null else mapNotNull.joinToString(separator = separator?.format(context) ?: "") { it.format(context) ?: "" }
	}
}

class DotListFormattable(vararg items: ContextFormattable?) : ListFormattable(separator = " • ".asFormattable(), *items)