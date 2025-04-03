package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.DimenRes
import com.dadino.quickstart3.color.ContextColor
import com.dadino.quickstart3.color.SurfaceColor

/**
 * An interface representing a drawable resource that is context-aware and can be
 * customized with properties like tint, animation, and surface appearance.
 *
 * Implementations of this interface provide the logic for retrieving drawables from a
 * [DrawableVault], handling resource retrieval based on the application context, and
 * applying visual customizations.
 */
interface ContextDrawable : Parcelable {
  fun getVaultId(context: Context): String

  @DimenRes
  fun getMaxSizeRes(): Int?

  /**
   * Retrieves the animation resource ID.
   *
   * @return The animation resource ID, or null if no animation is specified.
   */
  @AnimRes
  fun getAnimationRes(): Int?

  /**
   * Retrieves the tint color.
   *
   * @return A `ContextColor` object representing the tint color, or `null` if no tint color is defined for the current context.
   */
  fun getTint(): ContextColor?

  /**
   * Draws the current canvas content to the provided ImageView.  This function takes the current drawing canvas
   * and renders its contents onto the ImageView, effectively updating the visual display to reflect any
   * drawing actions that have been performed.  Note that this function does not save the canvas contents; it
   * only updates the ImageView's display.  If persistence is required, a separate saving mechanism is needed.
   *
   * @param imageView The ImageView to which the canvas content will be drawn.  This ImageView will be cleared
   *                 and repopulated with the current canvas drawing.  It should be a valid, visible ImageView
   *                 instance within the application's layout.
   */
  fun drawToImageView(imageView: ImageView)

  /**
   * Retrieves a Drawable associated with the current object, using the provided Context.
   *
   * This function delegates the actual Drawable retrieval to the `DrawableVault` class.  The specific
   * way the Drawable is determined (e.g., resource ID, asset path, etc.) is managed within `DrawableVault`
   * and is likely based on some inherent property or configuration of the current object (represented by `this`).
   *
   * @param context The Context from which to retrieve resources or access assets.  Crucial for interacting
   * with the Android system to load the Drawable.  Must not be null.
   * @return A Drawable associated with the object, or `null` if no Drawable can be found or loaded.
   */
  fun getDrawable(context: Context): Drawable? {
	return DrawableVault.getDrawable(context, this)
  }

  /**
   * Creates a drawable object suitable for display in the application.  The specific
   * type of drawable returned depends on the context and potentially other factors,
   * such as resource availability or system settings.
   *
   * @param context The application or activity context, used to access resources,
   *  theme attributes, and other system services.  This is crucial for correctly
   *  inflating drawables from resources or adapting them to the current device configuration.
   * @return A [Drawable] object, ready to be displayed, or null if drawable creation fails.
   *  Failure might occur due to invalid resource references, insufficient system resources,
   *  or other configuration issues.  Callers should handle null returns gracefully,
   *  potentially providing a fallback drawable or displaying an error message.
   *
   *  **Note:** The caller is responsible for managing the lifecycle of the returned Drawable,
   *  including ensuring that it is properly attached to a View before being displayed
   *  and released when no longer needed.
   */
  fun createDrawable(context: Context): Drawable?

  /**
   * Returns the [SurfaceColor] that this drawable is shown on top of.
   * This is used to determine the appropriate color for this surface.
   *
   * @return The [SurfaceColor] that this drawable is shown on top of.
   */
  fun getShownOn(): SurfaceColor

  /**
   * Creates a new [ContextDrawable] with the specified [shownOn] surface color.
   *
   * The [shownOn] surface color is used to determine the appropriate color for the drawable based on the current theme.
   * This is particularly useful when drawing on surfaces with varying background colors, ensuring sufficient contrast.
   *
   * @param shownOn The surface color the drawable will be displayed on.  Must be a [SurfaceColor].
   * @return A copy of this [ContextDrawable] with the specified [SurfaceColor].
   */
  fun withShownOn(shownOn: SurfaceColor): ContextDrawable

  /**
   * Applies a tint to the drawable.
   *
   * @param tint The color to tint the drawable with. If null, no tint is applied.
   * @return A copy of this [ContextDrawable] with the specified tint.
   */
  fun withTint(tint: ContextColor?): ContextDrawable
}

fun ImageView.drawContextDrawable(contextDrawable: ContextDrawable?) {
  when (contextDrawable) {
	null -> {
	  visibility = GONE

	  setImageDrawable(null)
	}

	else -> {
	  visibility = VISIBLE
	  contextDrawable.drawToImageView(this)
	}
  }

  contextDrawable?.getAnimationRes()?.let { this.startAnimation(AnimationUtils.loadAnimation(context, it)) }?.run { clearAnimation() }
}

