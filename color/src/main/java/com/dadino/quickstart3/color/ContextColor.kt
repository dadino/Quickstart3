package com.dadino.quickstart3.color

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.dadino.quickstart3.color.ContextColor.ColorStateList
import com.dadino.quickstart3.color.ContextColor.Hex
import com.dadino.quickstart3.color.ContextColor.Integer
import com.dadino.quickstart3.color.ContextColor.OnSurface
import com.dadino.quickstart3.color.ContextColor.Res
import kotlinx.parcelize.Parcelize

/**
 *  An interface representing a color that is dependent on the application context.
 *  This allows for dynamic color retrieval based on themes, resources, or other context-specific factors.
 *
 *  The interface provides methods to obtain a unique identifier for the color, retrieve the color
 *  as an integer representation, and generate a ColorStateList from the color.
 *
 *  It also includes several concrete implementations for different ways of defining a context-dependent color:
 *
 *  - [Res]: A color defined by a color resource ID.
 *  - [Hex]: A color defined by a hexadecimal string representation.
 *  - [Integer]: A color defined by an integer color value.
 *  - [OnSurface]: A color that is dynamically determined based on a provided [SurfaceColor].
 *  - [ColorStateList]: A color defined by an existing [ColorStateList].
 */
interface ContextColor : Parcelable {

  fun getId(context: Context): String

  /**
   * Retrieves the color associated with the current state.
   *
   * @param context The application or activity context.
   * @return The color as an integer representation (e.g., 0xFFFF0000 for red). The specific color returned depends on the internal state of the object and the context.
   *          It should be a valid Android color int value (e.g., retrieved from resources or using Color.rgb()).
   *          If no color is associated with the state, a default color value (typically transparent or white) might be returned, but this behavior is implementation-specific.
   */
  @ColorInt
  fun getColor(context: Context): Int

  /**
   * Generates a ColorStateList based on the provided context.
   *
   * This function utilizes the [ColorStateListGenerator] to create a [ColorStateList]
   * using the color obtained from the application's theme via [getColor].
   *
   * @param context The context used to access resources and theme information.
   * @return A [ColorStateList] representing the color state list generated.
   */
  fun getColorStateList(context: Context): android.content.res.ColorStateList = ColorStateListGenerator.createColorStateList(getColor(context))

  @Parcelize
  class Res(@ColorRes private val res: Int) : ContextColor {
	override fun getId(context: Context): String {
	  return "Res:$res:${context.resources.getResourceName(res)}"
	}

	override fun getColor(context: Context): Int {
	  return ContextCompat.getColor(context, res)
	}
  }

  /**
   * Represents a color defined by its hexadecimal string representation.
   *
   * This class allows you to specify a color using a hexadecimal string (e.g., "#FF0000" for red) and
   * provides methods to retrieve the color's ID and its integer representation usable by Android's
   * `Color` class.  It handles both hex strings with and without the leading "#" character.
   *
   * Example Usage:
   * ```kotlin
   * val redHex = Hex("#FF0000")
   * val blueHex = Hex("0000FF")
   *
   * val redColorInt = redHex.getColor(context)
   * val blueColorInt = blueHex.getColor(context)
   * ```
   *
   * @property hexString The hexadecimal string representation of the color.  Can include or exclude the leading "#".
   */
  @Parcelize
  class Hex(private val hexString: String) : ContextColor {
	override fun getId(context: Context): String {
	  return "Hex:$hexString"
	}

	override fun getColor(context: Context): Int {
	  return Color.parseColor(if (hexString.startsWith("#")) hexString else "#$hexString")
	}
  }

  /**
   * Represents a color defined by an integer value.
   *
   * This class encapsulates a color represented as an integer (@ColorInt) and provides
   * methods to retrieve its ID and color value in a context-independent manner.  It
   * implements the `ContextColor` interface, allowing it to be used wherever a
   * context-aware color representation is required.
   *
   * @property colorInt The integer representation of the color.  Must be a valid
   *                    color integer, typically obtained from resources or color constants.
   */
  @Parcelize
  class Integer(@ColorInt private val colorInt: Int) : ContextColor {
	override fun getId(context: Context): String {
	  return "Int:$colorInt"
	}

	override fun getColor(context: Context): Int {
	  return colorInt
	}
  }

  /**
   * Represents a color that should be used 'on' a specified surface color.  This class delegates
   * color resolution to [ColorOnSurfaceProvider], allowing for dynamic color determination based on
   * factors such as theme and system settings.
   *
   * @property surfaceColor The [SurfaceColor] whose 'on' color is desired. This dictates the background
   *                       context for which the 'on' color should be calculated.
   *
   * @see ContextColor
   * @see ColorOnSurfaceProvider
   * @see SurfaceColor
   */
  @Parcelize
  class OnSurface(private val surfaceColor: SurfaceColor) : ContextColor {
	override fun getId(context: Context): String {
	  return "OnSurface:$surfaceColor"
	}

	override fun getColor(context: Context): Int {
	  return ColorOnSurfaceProvider.getColorOn(surfaceColor, context) ?: Color.WHITE
	}
  }

  /**
   * Represents a color as a ColorStateList resource.
   *
   * This class encapsulates an Android ColorStateList, allowing it to be used and passed around
   * within the application while maintaining its color state information.  It implements the
   * `ContextColor` interface, providing methods to retrieve the color in different formats based on
   * the application context.
   *
   * @property colorStateList The underlying Android ColorStateList.
   */
  @Parcelize
  class ColorStateList(private val colorStateList: android.content.res.ColorStateList) : ContextColor {
	override fun getId(context: Context): String {
	  return "ColorStateList:$colorStateList"
	}

	override fun getColor(context: Context): Int {
	  return colorStateList.defaultColor
	}

	override fun getColorStateList(context: Context): android.content.res.ColorStateList {
	  return colorStateList
	}
  }
}

fun @receiver:androidx.annotation.ColorInt Int.asIntColor(): ContextColor = ContextColor.Integer(this)
fun @receiver:androidx.annotation.ColorRes Int.asResColor(): ContextColor = ContextColor.Res(this)
fun String.asHexColor(): ContextColor = ContextColor.Hex(this)
fun SurfaceColor.colorOn(): ContextColor = ContextColor.OnSurface(this)
