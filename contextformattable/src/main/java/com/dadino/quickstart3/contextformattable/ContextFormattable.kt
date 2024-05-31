package com.dadino.quickstart3.contextformattable

import android.content.Context

interface ContextFormattable {

  fun format(context: Context): CharSequence?

  override fun equals(other: Any?): Boolean
}

fun ContextFormattable?.isNullOrEmpty(context: Context): Boolean {
  val charSequence = this?.format(context)
  return this == null || charSequence.isNullOrBlank()
}