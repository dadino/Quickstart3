package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event


abstract class EventInterceptor {
	var isLoggingEnabled: Boolean = false
	abstract fun intercept(event: Event): Event
}