package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

data class Icon(
	@DrawableRes val icon: Int,
	@ColorRes val tint: Int? = null,
	@AnimRes val animation: Int? = null
) {

	fun asDrawable(context: Context): Drawable? {
		return ContextCompat.getDrawable(context, icon) //TODO tint
	}
}

fun @receiver:androidx.annotation.DrawableRes Int.asIcon(@ColorRes tint: Int? = null, @AnimRes animation: Int? = null): Icon = IconVault.getIcon(this, tint, animation)
fun @receiver:androidx.annotation.DrawableRes Int.asCompletableIcon(completed: Boolean, @AnimRes animation: Int? = null): Icon = IconVault.getIcon(this, tint = if (completed) R.color.complete else null, animation)
