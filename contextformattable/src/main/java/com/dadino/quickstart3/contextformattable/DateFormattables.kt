package com.dadino.quickstart3.contextformattable

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

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

fun Date.asFormattable() = DateFormattable(this)