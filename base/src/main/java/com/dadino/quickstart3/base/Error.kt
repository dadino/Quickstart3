package com.dadino.quickstart3.base

import android.content.Context

open class Error(val error: Throwable?, val formattable: ContextFormattable?) : ContextFormattable {

	override fun format(context: Context): CharSequence? {
		return formattable?.format(context)
	}
}


