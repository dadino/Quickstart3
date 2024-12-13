package com.dadino.quickstart3.contextformattable

import android.content.Context

interface ContextFormattable {

  fun format(context: Context, modifiers: List<CFModifier>): CharSequence?
  fun format(context: Context): CharSequence? = format(context, listOf())
  fun format(context: Context, modifier: CFModifier): CharSequence? = format(context, listOf(modifier))

  override fun equals(other: Any?): Boolean
}

fun ContextFormattable?.isNullOrEmpty(context: Context, modifiers: List<CFModifier> = listOf()): Boolean {
  val charSequence = this?.format(context, modifiers)
  return this == null || charSequence.isNullOrBlank()
}