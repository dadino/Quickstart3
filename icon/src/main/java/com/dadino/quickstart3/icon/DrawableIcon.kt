package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

data class DrawableIcon(
	private val drawable: Drawable,
	@ColorRes val tint: Int? = null,
	@AnimRes val animation: Int? = null,
	@DimenRes val maxSize: Int? = null,
	private val shownOn: ShownOn = ShownOn.SURFACE,
) : ContextDrawable {

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTintRes(): Int? = tint

  override fun createDrawable(context: Context): Drawable? {
	return drawable.let {
	  val temp = DrawableCompat.wrap(it.mutate())
	  if (tint != null) DrawableCompat.setTint(temp, ContextCompat.getColor(context, tint))
	  else DrawableCompat.setTintList(temp, when (shownOn) {
		ShownOn.PRIMARY   -> IconHandler.defaultIconTintOnPrimary
		ShownOn.SECONDARY -> IconHandler.defaultIconTintOnSecondary
		ShownOn.SURFACE   -> IconHandler.defaultIconTintOnSurface
	  })
	  temp
	}
  }

  override fun getShownOn(): ShownOn {
	return shownOn
  }

  override fun getVaultId(): String = "$drawable:$tint:$animation:$shownOn"

  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.FIT_CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }

  override fun withShownOn(shownOn: ShownOn): ContextDrawable {
	return this.copy(shownOn = shownOn)
  }
}

fun Drawable.asContextDrawable(@ColorRes tint: Int? = null, @AnimRes animation: Int? = null) = DrawableIcon(this, tint = tint, animation = animation)
