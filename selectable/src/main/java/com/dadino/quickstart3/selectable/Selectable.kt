package com.dadino.quickstart3.selectable

import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.icon.Icon

interface Selectable {

	fun getSelectionId(): Id
	fun getMainText(): ContextFormattable
	fun getSecondaryText(): ContextFormattable? = null
	fun getSelectionIcon(): Icon? = null
}

data class SimpleSelectable(
	private val id: Id,
	private val mainText: ContextFormattable,
	private val secondaryText: ContextFormattable? = null,
	private val icon: Icon? = null
) : Selectable {

	override fun getSelectionId(): Id {
		return id
	}

	override fun getMainText(): ContextFormattable {
		return mainText
	}

	override fun getSecondaryText(): ContextFormattable? {
		return secondaryText
	}

	override fun getSelectionIcon(): Icon? {
		return icon
	}
}

fun <T : Selectable> List<T>?.find(id: Id?): T? {
	if (this == null) return null
	if (id == null) return null
	return this.firstOrNull { it.getSelectionId() == id }
}