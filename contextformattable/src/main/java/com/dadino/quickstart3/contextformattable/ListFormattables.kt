package com.dadino.quickstart3.contextformattable

import android.content.Context
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
open class ListFormattable(private val separator: ContextFormattable?, private vararg val items: ContextFormattable?) : ContextFormattable {

  override fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence? {
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