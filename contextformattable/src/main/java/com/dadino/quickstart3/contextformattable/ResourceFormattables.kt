package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
open class ResFormattable(
  @StringRes private val textRes: Int,
  private vararg val args: @RawValue Any
) : ContextFormattable {

  override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
	return context.getString(textRes, *args)
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is ResFormattable) return false

	if (textRes != other.textRes) return false
	if (!args.contentEquals(other.args)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = textRes
	result = 31 * result + args.contentHashCode()
	return result
  }
}

@Parcelize
open class ExtendedResFormattable(
  @StringRes private val textRes: Int,
  private vararg val args: ContextFormattable
) : ContextFormattable {

  override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
	return context.getString(textRes, *args.mapNotNull { it.format(context) }.toTypedArray())
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is ExtendedResFormattable) return false

	if (textRes != other.textRes) return false
	if (!args.contentEquals(other.args)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = textRes
	result = 31 * result + args.contentHashCode()
	return result
  }
}

@Parcelize
open class PluralFormattable(
  @PluralsRes private val textRes: Int,
  private val quantity: Int,
  private vararg val args: String
) : ContextFormattable {

  override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
	return context.resources.getQuantityString(textRes, quantity, quantity, *args)
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is PluralFormattable) return false

	if (textRes != other.textRes) return false
	if (quantity != other.quantity) return false
	if (!args.contentEquals(other.args)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = textRes
	result = 31 * result + quantity
	result = 31 * result + args.contentHashCode()
	return result
  }
}

fun @receiver:StringRes Int.asFormattable() = ResFormattable(this)
fun @receiver:PluralsRes Int.asFormattable(count: Int) = PluralFormattable(this, count)