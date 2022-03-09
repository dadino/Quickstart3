package com.dadino.quickstart3.contextformattable

import android.content.Context

interface ContextFormattable {

	fun format(context: Context): CharSequence?
}

fun ContextFormattable?.isNullOrEmpty(context: Context) = this == null || this.format(context) == null