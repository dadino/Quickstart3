package com.dadino.quickstart3.contextformattable

import android.content.Context
import android.os.Parcelable
import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.aghajari.compose.text.ContentAnnotatedString
import com.aghajari.compose.text.asAnnotatedString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.io.Serializable

/**
 * An interface for objects that can be formatted into a CharSequence based on a given Android [Context] and optional [CFModifier]s.
 * Implementations of this interface should provide a way to represent themselves as a formatted string, taking into account
 * the provided context for localization, resource access, and other context-specific operations.
 *
 *  Key Features:
 *  - **Context-Aware Formatting:** Allows formatting to adapt to different contexts, enabling localization and access to context-specific resources.
 *  - **Modifier Support:**  Provides the ability to influence the formatting process through [CFModifier] objects, allowing for variations in output based on specific needs.
 *  - **Flexibility:** Supports formatting with no modifiers, a single modifier, or a list of modifiers, providing flexibility in how formatting is controlled.
 *
 *  Key Methods:
 *  - `format(context: Context, modifiers: List<CFModifier>): CharSequence?`:  The core formatting method. Takes an Android [Context] and a list of [CFModifier]s, returning the formatted [CharSequence] or `null` if formatting fails.
 *  - `format(context: Context): CharSequence?`:  A convenience method that formats using the given [Context] and an empty list of modifiers.  Equivalent to calling `format(context, listOf())`.
 *  - `format(context: Context, modifier: CFModifier): CharSequence?`:  A convenience method that formats using the given [Context] and a single [CFModifier]. Equivalent to calling `format(context, listOf(modifier))`.
 *
 *  Usage Examples:
 *  ```kotlin
 *  class MyFormattable(val value: Int) : ContextFormattable {
 *    override fun format(context: Context, modifiers: List<CFModifier>): CharSequence? {
 *      val formattedValue = if (modifiers.any { it is BoldModifier }) {
 *        "**$value**" // Example: apply bold formatting
 *      } else {
 *        value.toString()
 *      }
 *      return context.getString(R.string.my_format_string, formattedValue) // Example: localized string with placeholder
 *    }
 *
 *    // Example modifier for bold text (implementation omitted for brevity)
 *    object BoldModifier : CFModifier
 *  }
 *
 * */
@Stable
interface ContextFormattable : Parcelable, Serializable {
  fun format(context: Context, modifiers: ImmutableList<CFModifier>): CharSequence?
  fun format(context: Context): CharSequence? = format(context, persistentListOf())
  fun format(context: Context, modifier: CFModifier): CharSequence? = format(context, persistentListOf(modifier))

  @Composable
  fun asAnnotatedString(modifiers: ImmutableList<CFModifier> = persistentListOf()): ContentAnnotatedString? {
	val context = LocalContext.current
	val formatted: CharSequence? = remember(this, modifiers) { this.format(context, modifiers) }
	return remember(formatted) {
	  when (formatted) {
		null       -> null
		is Spanned -> formatted.asAnnotatedString()
		else       -> ContentAnnotatedString(AnnotatedString(formatted.toString()), inlineContents = listOf(), paragraphContents = listOf())
	  }
	}
  }

  override fun equals(other: Any?): Boolean
}

fun ContextFormattable?.isNullOrEmpty(context: Context, modifiers: ImmutableList<CFModifier> = persistentListOf()): Boolean {
  val charSequence = this?.format(context, modifiers)
  return this == null || charSequence.isNullOrBlank()
}