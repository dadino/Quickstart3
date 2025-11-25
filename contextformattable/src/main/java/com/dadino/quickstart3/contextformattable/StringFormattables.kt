package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Stable
open class StringFormattable(
  private val text: String,
  private vararg val args: @RawValue Any
) : ContextFormattable {

  override fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence? {
	return if (args.isNotEmpty()) String.format(text, *args) else text
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is StringFormattable) return false

	if (text != other.text) return false
	if (!args.contentEquals(other.args)) return false

	return true
  }

  override fun hashCode(): Int {
	var result = text.hashCode()
	result = 31 * result + args.contentHashCode()
	return result
  }
}

fun String.asFormattable(): ContextFormattable = StringFormattable(this)