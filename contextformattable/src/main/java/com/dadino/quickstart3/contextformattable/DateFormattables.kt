package com.dadino.quickstart3.contextformattable

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class DateFormattable(private val date: Date, private val dateFormat: String = "dd/MM/yyyy") : ContextFormattable {

  private val formatter by lazy { SimpleDateFormat(dateFormat, Locale.ITALIAN) }
  override fun format(context: Context): CharSequence? {
	return formatter.format(date)
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is DateFormattable) return false

	if (date != other.date) return false
	if (dateFormat != other.dateFormat) return false

	return true
  }

  override fun hashCode(): Int {
	var result = date.hashCode()
	result = 31 * result + dateFormat.hashCode()
	return result
  }
}

class DateRangeFormattable(private val fromDate: Date, private val toDate: Date, private val dateFormat: String = "dd/MM/yyyy") : ContextFormattable {

  private val formatter by lazy { SimpleDateFormat(dateFormat, Locale.ITALIAN) }
  override fun format(context: Context): CharSequence? {
	return "${formatter.format(fromDate)} - ${formatter.format(toDate)}"
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is DateRangeFormattable) return false

	if (fromDate != other.fromDate) return false
	if (toDate != other.toDate) return false
	if (dateFormat != other.dateFormat) return false

	return true
  }

  override fun hashCode(): Int {
	var result = fromDate.hashCode()
	result = 31 * result + toDate.hashCode()
	result = 31 * result + dateFormat.hashCode()
	return result
  }
}

fun Date.asFormattable() = DateFormattable(this)
fun Date.asFormattable(dateFormat: String) = DateFormattable(this, dateFormat = dateFormat)