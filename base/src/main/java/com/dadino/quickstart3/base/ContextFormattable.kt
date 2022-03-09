package com.dadino.quickstart3.base

import android.content.Context
import android.text.Spanned
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.*

interface ContextFormattable {

	fun format(context: Context): CharSequence?
}

open class ResFormattable(
	@StringRes private val textRes: Int,
	private vararg val args: Any
) : ContextFormattable {

	override fun format(context: Context): String? {
		return context.getString(textRes, *args)
	}
}

open class ExtendedResFormattable(
	@StringRes private val textRes: Int,
	private vararg val args: ContextFormattable
) : ContextFormattable {

	override fun format(context: Context): String? {
		return context.getString(textRes, *args.mapNotNull { it.format(context) }.toTypedArray())
	}
}

open class PluralFormattable(
	@PluralsRes private val textRes: Int,
	private val quantity: Int,
	private vararg val args: String
) : ContextFormattable {

	override fun format(context: Context): String? {
		return context.resources.getQuantityString(textRes, quantity, quantity, *args)
	}
}

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

class DateFormattable(private val date: Date, private val dateFormat: String = "dd/MM/yyyy") : ContextFormattable {

	private val formatter by lazy { SimpleDateFormat(dateFormat, Locale.ITALIAN) }
	override fun format(context: Context): CharSequence? {
		return formatter.format(date)
	}
}

class DateRangeFormattable(private val fromDate: Date, private val toDate: Date, private val dateFormat: String = "dd/MM/yyyy") : ContextFormattable {

	private val formatter by lazy { SimpleDateFormat(dateFormat, Locale.ITALIAN) }
	override fun format(context: Context): CharSequence? {
		return "${formatter.format(fromDate)} - ${formatter.format(toDate)}"
	}
}

fun String.asFormattable(): ContextFormattable = StringFormattable(this)

fun @receiver:androidx.annotation.StringRes Int.asFormattable() = ResFormattable(this)
fun @receiver:androidx.annotation.PluralsRes Int.asFormattable(count: Int) = PluralFormattable(this, count)

fun Date.asFormattable() = DateFormattable(this)
fun ContextFormattable?.isNullOrEmpty(context: Context) = this == null || this.format(context) == null