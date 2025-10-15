package com.dadino.quickstart3.icon

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat

@Composable
fun Drawable.toBitmapPainter(tintColor: Color? = null): BitmapPainter {
  return remember(this, tintColor) {
	val bitmap = this.toBitmap(tintColor)
	BitmapPainter(bitmap.asImageBitmap())
  }
}

private fun Drawable.toBitmap(tintColor: Color? = null): Bitmap {
  val drawable = DrawableCompat.wrap(this).mutate()
  tintColor?.let { drawable.setTint(it.toArgb()) }

  val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 24
  val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 24

  val bitmap = createBitmap(width, height)
  val canvas = Canvas(bitmap)
  drawable.setBounds(0, 0, width, height)
  drawable.draw(canvas)

  return bitmap
}