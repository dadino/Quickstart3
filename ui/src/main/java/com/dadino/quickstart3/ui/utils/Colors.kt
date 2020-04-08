package com.dadino.quickstart3.ui.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.dadino.quickstart3.ui.R

object Colors {
	@ColorInt
	fun getColorPrimary(context: Context): Int = getThemeColor(context, R.attr.colorPrimary)

	@ColorInt
	fun getColorPrimaryDark(context: Context): Int = getThemeColor(context, R.attr.colorPrimaryDark)

	@ColorInt
	fun getColorPrimaryVariant(context: Context): Int = getThemeColor(context, R.attr.colorPrimaryVariant)

	@ColorInt
	fun getColorSecondary(context: Context): Int = getThemeColor(context, R.attr.colorSecondary)

	@ColorInt
	fun getColorSecondaryVariant(context: Context): Int = getThemeColor(context, R.attr.colorSecondaryVariant)

	@ColorInt
	fun getColorSurface(context: Context): Int = getThemeColor(context, R.attr.colorSurface)

	@ColorInt
	fun getColorError(context: Context): Int = getThemeColor(context, R.attr.colorError)

	@ColorInt
	fun getColorOnSurface(context: Context): Int = getThemeColor(context, R.attr.colorOnSurface)

	@ColorInt
	fun getColorOnBackground(context: Context): Int = getThemeColor(context, R.attr.colorOnBackground)

	@ColorInt
	fun getColorOnPrimary(context: Context): Int = getThemeColor(context, R.attr.colorOnPrimary)

	@ColorInt
	fun getColorOnError(context: Context): Int = getThemeColor(context, R.attr.colorOnError)

	@ColorInt
	fun getColorOnPrimarySurface(context: Context): Int = getThemeColor(context, R.attr.colorOnPrimarySurface)

	@ColorInt
	fun getColorOnSecondary(context: Context): Int = getThemeColor(context, R.attr.colorOnSecondary)

	@ColorInt
	private fun getThemeColor(context: Context, colorId: Int): Int {
		val typedValue = TypedValue()

		val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(colorId))
		val color = a.getColor(0, 0)

		a.recycle()

		return color
	}
}