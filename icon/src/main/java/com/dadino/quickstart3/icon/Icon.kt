package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

data class Icon(
	@DrawableRes val icon: Int,
	@ColorRes val tint: Int? = null,
	@AnimRes val animation: Int? = null,
	@DimenRes val maxSize: Int? = R.dimen.default_icon_size,
) : ContextDrawable {

  private var drawable: Drawable? = null

  override fun getDrawable(context: Context): Drawable? {
	if (drawable == null) {
	  drawable = ContextCompat.getDrawable(context, icon)?.let {
		val temp = DrawableCompat.wrap(it.mutate())
		if (tint != null) DrawableCompat.setTint(temp, ContextCompat.getColor(context, tint))
		temp
	  }
	}
	return drawable
  }

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTintRes(): Int? = tint
  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }
}

fun @receiver:androidx.annotation.DrawableRes Int.asIcon(@ColorRes tint: Int? = null, @AnimRes animation: Int? = null): Icon = IconVault.getIcon(this, tint, animation)
fun @receiver:androidx.annotation.DrawableRes Int.asCompletableIcon(completed: Boolean, @AnimRes animation: Int? = null): Icon = IconVault.getIcon(this, tint = if (completed) R.color.complete else null, animation)
