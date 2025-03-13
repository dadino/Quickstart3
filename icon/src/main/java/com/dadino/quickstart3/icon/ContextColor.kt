package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize

sealed class ContextColor(val id: String) : Parcelable {
  @ColorInt
  abstract fun getColor(context: Context): Int

  @Parcelize
  class Res(@ColorRes private val res: Int) : ContextColor("Res:$res") {
	override fun getColor(context: Context): Int {
	  return ContextCompat.getColor(context, res)
	}
  }

  @Parcelize
  class Hex(private val hexString: String) : ContextColor("Hex:$hexString") {
	override fun getColor(context: Context): Int {
	  return Color.parseColor(if (hexString.startsWith("#")) hexString else "#$hexString")
	}
  }

  @Parcelize
  class Integer(@ColorInt private val colorInt: Int) : ContextColor("Int:$colorInt") {
	override fun getColor(context: Context): Int {
	  return colorInt
	}
  }
}

fun @receiver:androidx.annotation.ColorInt Int.asIntColor(): ContextColor = ContextColor.Integer(this)
fun @receiver:androidx.annotation.ColorRes Int.asResColor(): ContextColor = ContextColor.Res(this)
fun String.asHexColor(): ContextColor = ContextColor.Hex(this)
