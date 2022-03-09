package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.base.Event

interface InteractionEventSourceWithManager : InteractionEventSource {
	fun receiveEvent(event: Event)
}