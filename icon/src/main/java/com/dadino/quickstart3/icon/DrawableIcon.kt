package com.dadino.quickstart3.icon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.DimenRes
import androidx.core.graphics.drawable.DrawableCompat
import com.dadino.quickstart3.color.ColorOnSurfaceProvider
import com.dadino.quickstart3.color.ContextColor
import com.dadino.quickstart3.color.SurfaceColor
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Represents an icon defined by a drawable resource.  Implements [ContextDrawable] for
 * easy handling of drawables that require context for creation, theming, or identification.
 *
 * @property drawable The actual [Drawable] object.  Marked as `@RawValue` for Parcelization due
 *  to potential limitations with standard Drawable parcelization.
 * @property tint Optional [ContextColor] to apply as a tint to the drawable. If null, the
 *  drawable will be tinted based on the [shownOn] surface color.
 * @property animation Optional resource ID of an animation to apply to the drawable.
 * @property maxSize Optional dimension resource ID defining the maximum size of the drawable.
 * @property shownOn The [SurfaceColor] indicating the background surface on which this icon will
 *  be displayed.  This influences the default tint if no explicit tint is provided.  Defaults to SURFACE().
 */
@Parcelize
data class DrawableIcon(
  private val drawable: @RawValue Drawable,
  private val tint: ContextColor? = null,
  @AnimRes val animation: Int? = null,
  @DimenRes val maxSize: Int? = null,
  private val shownOn: SurfaceColor = SurfaceColor.SURFACE(),
) : ContextDrawable {

  override fun getAnimationRes(): Int? = animation
  override fun getMaxSizeRes(): Int? = maxSize
  override fun getTint(): ContextColor? = tint

  override fun createDrawable(context: Context): Drawable {
	return drawable.let {
	  val temp = DrawableCompat.wrap(it.mutate())
	  DrawableCompat.setTintList(temp, getTintList(context))
	  temp
	}
  }

  override fun getShownOn(): SurfaceColor {
	return shownOn
  }

  override fun getVaultId(context: Context): String = "Drawable:$drawable:${tint?.getId(context)}:${animation?.let { context.resources.getResourceName(it) }}:$shownOn"

  /**
   * Draws the drawable associated with this component to the given ImageView.
   *
   * This function sets the ImageView's scale type to FIT_CENTER to ensure the drawable
   * is centered and scaled proportionally within the ImageView's bounds. It then retrieves
   * the drawable using `getDrawable()` (which should be implemented by the specific
   * component subclass) and sets it as the ImageView's drawable.
   *
   * @param imageView The ImageView to draw the drawable to.  Must not be null.
   */
  override fun drawToImageView(imageView: ImageView) {
	imageView.scaleType = ImageView.ScaleType.FIT_CENTER
	imageView.setImageDrawable(getDrawable(imageView.context))
  }

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

fun Drawable.asContextDrawable(tint: ContextColor? = null, @AnimRes animation: Int? = null) = DrawableIcon(this, tint = tint, animation = animation)
