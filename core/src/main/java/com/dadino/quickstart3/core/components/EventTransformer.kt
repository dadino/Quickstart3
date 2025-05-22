package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.utils.QuickLogger

abstract class EventTransformer(var enableLogging: Boolean = false) {

  /**
   * Transform an Event to a different Event

   * @return - the tranformed Event or null if no transformation happened
   **/
  protected abstract fun transform(event: Event): Event?

  fun performTransform(event: Event): Event {
	val transformed = transform(event)
	if (transformed != null && enableLogging) QuickLogger.tag(javaClass.simpleName).d { ">>> ${event.javaClass.simpleName} ~~~ ${transformed.javaClass.simpleName} >>>" }
	return transform(event) ?: event
  }
}