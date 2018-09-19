package com.dadino.quickstart3.core.adapters

import android.util.Log
import com.dadino.quickstart3.core.adapters.holders.BaseHolder
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.entities.UserActionable
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

abstract class BaseAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : android.support.v7.widget.RecyclerView.Adapter<HOLDER>()
		, UserActionable {

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
				holder.userActions()
						.doOnNext { Log.d("Adapter", "<--- ${System.nanoTime()} Action: $it") }
						.subscribe(userActionsOnItems)
		)
	}

	override fun userActions(): Observable<UserAction> {
		return userActions
	}

}
