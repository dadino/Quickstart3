package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

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

fun @receiver:StringRes Int.asFormattable() = ResFormattable(this)
fun @receiver:PluralsRes Int.asFormattable(count: Int) = PluralFormattable(this, count)