package com.dadino.quickstart3.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

object VectorCompat {

	fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
		return try {
			ContextCompat.getDrawable(context, drawableRes)
		} catch (e: Exception) {
			VectorDrawableCompat.create(context.resources, drawableRes, null)
		}

	}
}