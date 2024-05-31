package com.dadino.quickstart3.contextformattable

import android.content.Context

open class Error(val error: Throwable?, val formattable: ContextFormattable?) : ContextFormattable {

	override fun format(context: Context): CharSequence? {
		return formattable?.format(context)
	}

  override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (other !is Error) return false

	if (error != other.error) return false
	if (formattable != other.formattable) return false

	return true
  }

  override fun hashCode(): Int {
	var result = error?.hashCode() ?: 0
	result = 31 * result + (formattable?.hashCode() ?: 0)
	return result
  }
}


