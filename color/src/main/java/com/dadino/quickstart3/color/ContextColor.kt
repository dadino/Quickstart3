package com.dadino.quickstart3.color

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize

interface ContextColor : Parcelable {

  fun getId(context: Context): String

  @ColorInt
  fun getColor(context: Context): Int

  fun getColorStateList(context: Context): android.content.res.ColorStateList = ColorStateListGenerator.createColorStateList(getColor(context))

  @Parcelize
  class Res(@ColorRes private val res: Int) : ContextColor {
	override fun getId(context: Context): String {
	  return "Res:$res:${context.resources.getResourceName(res)}"
	}

	override fun getColor(context: Context): Int {
	  return ContextCompat.getColor(context, res)
	}
  }

  @Parcelize
  class Hex(private val hexString: String) : ContextColor {
	override fun getId(context: Context): String {
	  return "Hex:$hexString"
	}

	override fun getColor(context: Context): Int {
	  return Color.parseColor(if (hexString.startsWith("#")) hexString else "#$hexString")
	}
  }

  @Parcelize
  class Integer(@ColorInt private val colorInt: Int) : ContextColor {
	override fun getId(context: Context): String {
	  return "Int:$colorInt"
	}

	override fun getColor(context: Context): Int {
	  return colorInt
	}
  }

  @Parcelize
  class OnSurface(private val surfaceColor: SurfaceColor) : ContextColor {
	override fun getId(context: Context): String {
	  return "OnSurface:$surfaceColor"
	}

	override fun getColor(context: Context): Int {
	  return ColorOnSurfaceProvider.getColorOn(surfaceColor, context) ?: Color.WHITE
	}
  }

  @Parcelize
  class ColorStateList(private val colorStateList: android.content.res.ColorStateList) : ContextColor {
	override fun getId(context: Context): String {
	  return "ColorStateList:$colorStateList"
	}

	override fun getColor(context: Context): Int {
	  return colorStateList.defaultColor
	}

	override fun getColorStateList(context: Context): android.content.res.ColorStateList {
	  return colorStateList
	}
  }
}

fun @receiver:androidx.annotation.ColorInt Int.asIntColor(): ContextColor = ContextColor.Integer(this)
fun @receiver:androidx.annotation.ColorRes Int.asResColor(): ContextColor = ContextColor.Res(this)
fun String.asHexColor(): ContextColor = ContextColor.Hex(this)
fun SurfaceColor.colorOn(): ContextColor = ContextColor.OnSurface(this)
