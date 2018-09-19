package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.Event
import io.reactivex.Observable

interface InteractionEventSource {
	fun interactionEvents(): Observable<Event>
}