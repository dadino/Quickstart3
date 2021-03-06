package com.dadino.quickstart3.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.InteractionEventSource
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : RecyclerView.Adapter<HOLDER>(), InteractionEventSource {

	private val interactionEventsOnItemsRelay = PublishRelay.create<Event>()
	private val interactionEvents: Observable<Event> by lazy {
		interactionEventsOnItemsRelay
			.doOnDispose {
				holderListeners.clear()
			}
	}

	private val holderListeners = CompositeDisposable()

	fun attachListenerToHolder(holder: HOLDER) {
		holderListeners.add(
			holder.interactionEvents()
				.subscribe(interactionEventsOnItemsRelay)
		)
	}

	override fun interactionEvents(): Observable<Event> {
		return interactionEvents
	}

}
