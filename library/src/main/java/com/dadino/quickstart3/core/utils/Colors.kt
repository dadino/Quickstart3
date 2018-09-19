package com.dadino.quickstart3.core.utils

import android.content.Context
import android.support.annotation.ColorInt
import android.util.TypedValue
import com.dadino.quickstart3.core.R


object Colors {
	@ColorInt
	fun getAccentColor(context: Context): Int = getThemeColor(context, R.attr.colorAccent)

	@ColorInt
	fun getPrimaryColor(context: Context): Int = getThemeColor(context, R.attr.colorPrimary)

	@ColorInt
	fun getPrimaryDarkColor(context: Context): Int = getThemeColor(context, R.attr.colorPrimaryDark)

	@ColorInt
	private fun getThemeColor(context: Context, colorId: Int): Int {
		val typedValue = TypedValue()

		val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(colorId))
		val color = a.getColor(0, 0)

		a.recycle()

		return color
	}
}