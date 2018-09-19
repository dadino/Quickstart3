package com.dadino.quickstart3.core.utils

import com.dadino.quickstart3.core.entities.Optional


fun <T : Collection<*>> T?.isNullOrEmpty(): Boolean {
	return this == null || this.isEmpty()
}

fun <E, T : Collection<E>> T.firstOptional(): Optional<E> {
	return Optional.create(firstOrNull())
}

inline fun <E, T : List<E>> T.toggleElement(element: E, crossinline compare: T.(E) -> Boolean): List<E> {
	val elementInList = firstOrNull { compare(it) }
	val newCompList = arrayListOf<E>()
	newCompList.addAll(this)
	if (elementInList == null) {
		newCompList.add(element)
	} else {
		newCompList.remove(elementInList)
	}
	return newCompList.toList()
}

inline fun <E, T : List<E>> T.modifyElement(crossinline compare: T.(E) -> Boolean, crossinline change: T.(E) -> E): List<E> {
	val elementInList = firstOrNull { compare(it) }

	if (elementInList != null) {
		change(elementInList)
	}
	return this
}
