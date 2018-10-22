package com.dadino.quickstart3.core.components

import android.util.Log
import com.dadino.quickstart3.core.entities.Event


abstract class EventTransformer(var enableLogging: Boolean = false) {

	/**
	 * Transform an Event to a different Event

	 * @return - the tranformed Event or null if no transformation happened
	 **/
	protected abstract fun transform(event: Event): Event?

	fun performTransform(event: Event): Event {
		val transformed = transform(event)
		if (transformed != null) log { ">>> ${event.javaClass.simpleName} ~~~ ${transformed.javaClass.simpleName} >>>" }
		return transform(event) ?: event
	}

	private fun log(createMessage: () -> String) {
		if (enableLogging) Log.d(javaClass.simpleName, createMessage())
	}
}