package com.dadino.quickstart3.ui.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.core.components.InteractionEventSource
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : RecyclerView.Adapter<HOLDER>()
		, InteractionEventSource {

	private val userActionsOnItems = PublishRelay.create<UserAction>()
	private val userActions: Observable<UserAction> by lazy {
		userActionsOnItems
				.doOnDispose {
					holderListeners.dispose()
				}
				.doOnNext { Log.d("Adapter", "---> ${System.nanoTime()} Action: $it") }
	}

	private val holderListeners = CompositeDisposable()

	fun attachListenerToHolder(holder: HOLDER) {
		holderListeners.add(
				holder.interactionEvents()
						.doOnNext { Log.d("Adapter", "<--- ${System.nanoTime()} Action: $it") }
						.subscribe(userActionsOnItems)
		)
	}

	override fun interactionEvents(): Observable<UserAction> {
		return userActions
	}

}
