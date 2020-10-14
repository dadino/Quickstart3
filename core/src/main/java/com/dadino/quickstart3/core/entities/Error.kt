package com.dadino.quickstart3.core.entities

import android.content.Context
import com.dadino.quickstart3.core.components.ContextFormattable

open class Error(val error: Throwable?, val formattable: ContextFormattable?) : ContextFormattable {

	override fun format(context: Context): CharSequence? {
		return formattable?.format(context)
	}
}


