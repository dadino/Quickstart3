package com.dadino.quickstart3.color

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorInt

object ColorOnSurfaceProvider {
  var tintOnSurfaceRepository: IColorOnSurfaceRepository? = null

  fun getColorStateListOn(surfaceColor: SurfaceColor, context: Context): ColorStateList? {
	return tintOnSurfaceRepository?.getColorStateListOn(surfaceColor, context)
  }

  @ColorInt
  fun getColorOn(surfaceColor: SurfaceColor, context: Context): Int? {
	return tintOnSurfaceRepository?.getColorOn(surfaceColor, context)
  }
}

interface IColorOnSurfaceRepository {
  fun getColorStateListOn(surfaceColor: SurfaceColor, context: Context): ColorStateList? {
	return when (surfaceColor) {
	  is SurfaceColor.PRIMARY         -> getColorStateListOnPrimary(context, surfaceColor.muted)
	  is SurfaceColor.SECONDARY       -> getColorStateListOnSecondary(context, surfaceColor.muted)
	  is SurfaceColor.SURFACE         -> getColorStateListOnSurface(context, surfaceColor.muted)
	  is SurfaceColor.BACKGROUND      -> getColorStateListOnBackground(context, surfaceColor.muted)
	  is SurfaceColor.ERROR           -> getColorStateListOnError(context, surfaceColor.muted)
	  is SurfaceColor.PRIMARY_SURFACE -> getColorStateListOnPrimarySurface(context, surfaceColor.muted)
	}
  }

  @ColorInt
  fun getColorOn(surfaceColor: SurfaceColor, context: Context): Int? {
	return when (surfaceColor) {
	  is SurfaceColor.PRIMARY         -> getColorOnPrimary(context, surfaceColor.muted)
	  is SurfaceColor.SECONDARY       -> getColorOnSecondary(context, surfaceColor.muted)
	  is SurfaceColor.SURFACE         -> getColorOnSurface(context, surfaceColor.muted)
	  is SurfaceColor.BACKGROUND      -> getColorOnBackground(context, surfaceColor.muted)
	  is SurfaceColor.ERROR           -> getColorOnError(context, surfaceColor.muted)
	  is SurfaceColor.PRIMARY_SURFACE -> getColorOnPrimarySurface(context, surfaceColor.muted)
	}
  }

  fun getColorStateListOnSurface(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnSurface(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  fun getColorStateListOnPrimary(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnPrimary(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  fun getColorStateListOnSecondary(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnSecondary(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  fun getColorStateListOnError(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnError(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  fun getColorStateListOnBackground(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnBackground(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  fun getColorStateListOnPrimarySurface(context: Context, muted: Boolean): ColorStateList? {
	return getColorOnPrimarySurface(context, muted)?.let { ColorStateList.valueOf(it) }
  }

  @ColorInt
  fun getColorOnSurface(context: Context, muted: Boolean): Int?

  @ColorInt
  fun getColorOnPrimary(context: Context, muted: Boolean): Int?

  @ColorInt
  fun getColorOnSecondary(context: Context, muted: Boolean): Int?

  @ColorInt
  fun getColorOnError(context: Context, muted: Boolean): Int?

  @ColorInt
  fun getColorOnBackground(context: Context, muted: Boolean): Int?

  @ColorInt
  fun getColorOnPrimarySurface(context: Context, muted: Boolean): Int?
}