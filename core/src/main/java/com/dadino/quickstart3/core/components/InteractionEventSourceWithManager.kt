package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event

/**
 * An [InteractionEventSource] that also has the ability to directly receive events
 * through a manager or other controlling entity.  This allows for external sources
 * (like input devices) to feed events into the interaction system.
 */
interface InteractionEventSourceWithManager : InteractionEventSource {
	fun receiveEvent(event: Event)
}