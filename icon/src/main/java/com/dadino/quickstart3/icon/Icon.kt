package com.dadino.quickstart3.icon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dadino.quickstart3.color.ColorOnSurfaceProvider
import com.dadino.quickstart3.color.ContextColor
import com.dadino.quickstart3.color.SurfaceColor
import kotlinx.parcelize.Parcelize

/**
 * Represents an icon drawable with associated properties like tint, animation, size, and surface color.
 *
 * Implements [ContextDrawable] to provide drawable creation, animation, and tinting based on the context.
 *
 * @property icon The drawable resource ID of the icon.
 * @property tint Optional tint color for the icon, represented by a [ContextColor]. If null, the tint will be automatically determined based on the [shownOn] surface color.
 * @property animation Optional animation resource ID for the icon.
 * @property maxSize Optional maximum size dimension resource ID for the icon. Defaults to `R.dimen.default_icon_size`.
 * @property shownOn The surface color on which the icon will be displayed. This affects the default tint when no explicit tint is provided.
 */
@Parcelize
data class Icon(
  @DrawableRes val icon: Int,
  private val tint: ContextColor? = null,
  @AnimRes val animation: Int? = null,
  @DimenRes val maxSize: Int? = R.dimen.default_icon_size,
  private val shownOn: SurfaceColor = SurfaceColor.SURFACE(),
) : ContextDrawable {

  override fun createDrawable(context: Context): Drawable? {
	return ContextCompat.getDrawable(context, icon)?.let { baseDrawable ->
	  val temp = DrawableCompat.wrap(baseDrawable.mutate())
	  DrawableCompat.setTintList(temp, getTintList(context))
	  temp
	}
  }

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTint(): ContextColor? = tint

  /**
   * Draws the drawable representation of this icon to the provided ImageView.
   *
   * This function sets the ImageView's scale type to CENTER and then sets the drawable
   * as the image for the ImageView.  The drawable is obtained from the context of the
   * ImageView.
   *
   * @param imageView The ImageView to draw the icon onto.
   */
  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }

  override fun getShownOn(): SurfaceColor {
	return shownOn
  }

  override fun getVaultId(context: Context): String =
	"Icon:${context.resources.getResourceName(icon)}:${tint?.getId(context)}:${animation?.let { context.resources.getResourceName(it) }}:$shownOn"

  override fun withShownOn(shownOn: SurfaceColor): ContextDrawable {
	return this.copy(shownOn = shownOn)
  }

  override fun withTint(tint: ContextColor?): ContextDrawable {
	return this.copy(tint = tint)
  }

  private fun getTintList(context: Context): ColorStateList? {
	return if (tint != null) ColorStateList.valueOf(tint.getColor(context))
	else ColorOnSurfaceProvider.getColorStateListOn(shownOn, context)
  }
}

fun @receiver:androidx.annotation.DrawableRes Int.asIcon(tint: ContextColor? = null, @AnimRes animation: Int? = null, shownOn: SurfaceColor = SurfaceColor.SURFACE()): Icon =
  Icon(this, tint, animation, shownOn = shownOn)

fun @receiver:androidx.annotation.DrawableRes Int.asIcon(@ColorRes tint: Int?, @AnimRes animation: Int? = null, shownOn: SurfaceColor = SurfaceColor.SURFACE()): Icon =
  Icon(this, tint?.let { ContextColor.Res(it) }, animation, shownOn = shownOn)

fun @receiver:androidx.annotation.DrawableRes Int.asCompletableIcon(completed: Boolean, @AnimRes animation: Int? = null, shownOn: SurfaceColor = SurfaceColor.SURFACE()): Icon =
  Icon(this, tint = if (completed) ContextColor.Res(R.color.complete) else null, animation, shownOn = shownOn)
