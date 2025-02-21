package com.dadino.quickstart3.icon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DrawableIcon(
  private val drawable: @RawValue Drawable,
  @ColorRes val tint: Int? = null,
  @AnimRes val animation: Int? = null,
  @DimenRes val maxSize: Int? = null,
  private val shownOn: SurfaceColor = SurfaceColor.SURFACE,
) : ContextDrawable {

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTintRes(): Int? = tint

  override fun createDrawable(context: Context): Drawable? {
	return drawable.let {
	  val temp = DrawableCompat.wrap(it.mutate())
	  DrawableCompat.setTintList(temp, getTintList(context))
	  temp
	}
  }

  override fun getShownOn(): SurfaceColor {
	return shownOn
  }

  override fun getVaultId(context: Context): String = "Drawable:$drawable:${tint?.let { context.resources.getResourceName(it) }}:${animation?.let { context.resources.getResourceName(it) }}:$shownOn"

  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.FIT_CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }

  override fun withShownOn(shownOn: SurfaceColor): ContextDrawable {
	return this.copy(shownOn = shownOn)
  }

  private fun getTintList(context: Context): ColorStateList? {
	return if (tint != null) ContextCompat.getColorStateList(context, tint)
	else when (shownOn) {
	  SurfaceColor.PRIMARY         -> IconHandler.defaultIconTintOnPrimary
	  SurfaceColor.SECONDARY       -> IconHandler.defaultIconTintOnSecondary
	  SurfaceColor.SURFACE         -> IconHandler.defaultIconTintOnSurface
	  SurfaceColor.BACKGROUND      -> IconHandler.defaultIconTintOnBackground
	  SurfaceColor.ERROR           -> IconHandler.defaultIconTintOnError
	  SurfaceColor.PRIMARY_SURFACE -> IconHandler.defaultIconTintOnPrimarySecondary
	}
  }
}

fun Drawable.asContextDrawable(@ColorRes tint: Int? = null, @AnimRes animation: Int? = null) = DrawableIcon(this, tint = tint, animation = animation)
