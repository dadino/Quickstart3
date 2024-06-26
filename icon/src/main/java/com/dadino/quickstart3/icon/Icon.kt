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
  private val shownOn: SurfaceColor = SurfaceColor.SURFACE,
) : ContextDrawable {

  override fun createDrawable(context: Context): Drawable? {
	return ContextCompat.getDrawable(context, icon)?.let {
	  val temp = DrawableCompat.wrap(it.mutate())
	  if (tint != null) DrawableCompat.setTint(temp, ContextCompat.getColor(context, tint))
	  else DrawableCompat.setTintList(
		temp, when (shownOn) {
		  SurfaceColor.PRIMARY         -> IconHandler.defaultIconTintOnPrimary
		  SurfaceColor.SECONDARY       -> IconHandler.defaultIconTintOnSecondary
		  SurfaceColor.SURFACE         -> IconHandler.defaultIconTintOnSurface
		  SurfaceColor.BACKGROUND      -> IconHandler.defaultIconTintOnBackground
		  SurfaceColor.ERROR           -> IconHandler.defaultIconTintOnError
		  SurfaceColor.PRIMARY_SURFACE -> IconHandler.defaultIconTintOnPrimarySecondary
		}
	  )
	  temp
	}
  }

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTintRes(): Int? = tint
  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }

  override fun getShownOn(): SurfaceColor {
	return shownOn
  }

  override fun getVaultId(): String = "$icon:$tint:$animation:$shownOn"
  override fun withShownOn(shownOn: SurfaceColor): ContextDrawable {
	return this.copy(shownOn = shownOn)
  }
}

fun @receiver:androidx.annotation.DrawableRes Int.asIcon(@ColorRes tint: Int? = null, @AnimRes animation: Int? = null, shownOn: SurfaceColor = SurfaceColor.SURFACE): Icon =
  Icon(this, tint, animation, shownOn = shownOn)

fun @receiver:androidx.annotation.DrawableRes Int.asCompletableIcon(completed: Boolean, @AnimRes animation: Int? = null, shownOn: SurfaceColor = SurfaceColor.SURFACE): Icon =
  Icon(this, tint = if (completed) R.color.complete else null, animation, shownOn = shownOn)
