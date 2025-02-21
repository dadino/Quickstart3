package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

interface ContextDrawable : Parcelable {
  fun getVaultId(context: Context): String

  @DimenRes
  fun getMaxSizeRes(): Int?

  @AnimRes
  fun getAnimationRes(): Int?

  @ColorRes
  fun getTintRes(): Int?
  fun drawToImageView(imageView: ImageView)
  fun getDrawable(context: Context): Drawable? {
	return DrawableVault.getDrawable(context, this)
  }

  fun createDrawable(context: Context): Drawable?
  fun getShownOn(): SurfaceColor

  fun withShownOn(shownOn: SurfaceColor): ContextDrawable
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

