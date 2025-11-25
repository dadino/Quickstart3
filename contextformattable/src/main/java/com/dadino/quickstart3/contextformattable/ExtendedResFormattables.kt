package com.dadino.quickstart3.contextformattable

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.aghajari.compose.text.ContentAnnotatedString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
open class ExtendedResFormattable(
  @StringRes private val textRes: Int,
  private vararg val args: ContextFormattable
) : ContextFormattable {

  override fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence? {
	return context.getString(textRes, *args.mapNotNull { it.format(context) }.toTypedArray())
  }

  @Composable
  override fun asAnnotatedString(modifiers: ImmutableList<CFModifier>): ContentAnnotatedString? {
	val text = stringResource(textRes, *args.mapNotNull { it.asAnnotatedString(modifiers) }.toTypedArray())
	return remember(text) {
	  ContentAnnotatedString(
		annotatedString = AnnotatedString(text),
		inlineContents = listOf(),
		paragraphContents = listOf()
	  )
	}
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
open class ExtendedPluralFormattable(
  @PluralsRes private val textRes: Int,
  private val quantity: Int,
  private vararg val args: ContextFormattable
) : ContextFormattable {

  override fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence? {
	return context.resources.getQuantityString(textRes, quantity, quantity, *args.mapNotNull { it.format(context) }.toTypedArray())
  }

  @Composable
  override fun asAnnotatedString(modifiers: ImmutableList<CFModifier>): ContentAnnotatedString? {
	val text = pluralStringResource(
	  id = textRes,
	  count = quantity,
	  quantity,
	  *args.mapNotNull { it.asAnnotatedString() }.toTypedArray()
	)
	return remember(text) {
	  ContentAnnotatedString(
		AnnotatedString(text),
		listOf(),
		listOf()
	  )
	}
  }

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is ExtendedPluralFormattable) return false

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