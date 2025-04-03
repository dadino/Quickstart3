package com.dadino.quickstart3.color

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.graphics.ColorUtils

object ColorStateListGenerator {

  /**
   * Generates a ColorStateList from a single base color.
   *
   * @param baseColor The starting color.
   * @return A ColorStateList with applied filters for different states.
   */
  fun createColorStateList(baseColor: Int): ColorStateList {
	// Define state arrays for different view states.
	// 1. Pressed state
	// 2. Focused state
	// 3. Disabled state (when the view is not enabled)
	// 4. Default state (no specific state)
	val states = arrayOf(
	  intArrayOf(android.R.attr.state_pressed),    // pressed
	  intArrayOf(android.R.attr.state_focused),    // focused
	  intArrayOf(-android.R.attr.state_enabled),   // disabled
	  intArrayOf()                                 // default
	)

	// Generate colors by applying filters to the base color.
	val colors = intArrayOf(
	  darkenColor(baseColor, 0.8f),    // Darken for pressed state.
	  lightenColor(baseColor, 1.2f),   // Lighten for focused state.
	  adjustAlpha(baseColor, 0.5f),    // Reduce alpha for disabled state.
	  baseColor                        // Default state uses the base color.
	)

	// Return the constructed ColorStateList.
	return ColorStateList(states, colors)
  }

  /**
   * Darkens a color by blending it with black.
   *
   * @param color The original color.
   * @param factor The factor to darken the color (values < 1 darken more).
   * @return The darkened color.
   */
  fun darkenColor(color: Int, factor: Float): Int {
	// Blend the color with black. A lower factor means more darkening.
	return ColorUtils.blendARGB(color, Color.BLACK, 1 - factor)
  }

  /**
   * Lightens a color by blending it with white.
   *
   * @param color The original color.
   * @param factor The factor to lighten the color (values > 1 lighten it).
   * @return The lightened color.
   */
  fun lightenColor(color: Int, factor: Float): Int {
	// Blend the color with white. The amount of blending is factor - 1.
	return ColorUtils.blendARGB(color, Color.WHITE, factor - 1)
  }

  /**
   * Adjusts the alpha component of a color.
   *
   * @param color The original color.
   * @param factor The factor by which to multiply the alpha (0.0f to 1.0f).
   * @return The color with adjusted transparency.
   */
  fun adjustAlpha(color: Int, factor: Float): Int {
	val alpha = (Color.alpha(color) * factor).toInt()
	return (color and 0x00FFFFFF) or (alpha shl 24)
  }
}