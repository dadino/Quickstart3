package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.contextformattable.ContextFormattable

interface StateWithTitle {

	fun getTitle(): ContextFormattable?
	fun getSubtitle(): ContextFormattable? = null
}