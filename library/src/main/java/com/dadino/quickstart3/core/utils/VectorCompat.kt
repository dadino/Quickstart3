package com.dadino.quickstart3.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat

object VectorCompat {

	fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
		return try {
			ContextCompat.getDrawable(context, drawableRes)
		} catch (e: Exception) {
			VectorDrawableCompat.create(context.resources, drawableRes, null)
		}

	}
}