package com.dadino.quickstart3.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import androidx.annotation.LayoutRes
import com.dadino.quickstart3.core.components.InteractionEventSource
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

abstract class BaseSpinnerAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : android.widget.BaseAdapter(), SpinnerAdapter, InteractionEventSource {
	var items: List<ITEM> = ArrayList()
		set(value) {
			field = value
			count = -1
			notifyDataSetChanged()
		}

	private var count: Int = 0
	private var inflater: LayoutInflater? = null

	protected fun inflater(context: Context): LayoutInflater {
		if (inflater == null) inflater = LayoutInflater.from(context)
		return inflater!!
	}

	val additionalItemCount: Int
		get() = 0

	override fun getCount(): Int {
		if (count >= 0) return count

		count = items.size + additionalItemCount
		return count

	}

	fun findItem(position: Int): ITEM? {
		return if (position in 0 until items.size) items[position] else null
	}

	override fun getItem(position: Int): ITEM {
		return items[position]
	}

	abstract override fun getItemId(position: Int): Long

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		var convertview = convertView
		val viewHolder: HOLDER
		if (convertview == null) {
			convertview = inflateView(inflater(parent.context), parent)
			viewHolder = createHolder(convertview)
			attachListenerToHolder(viewHolder)
			convertview.tag = viewHolder
		} else {
			viewHolder = convertview.tag as HOLDER
		}
		viewHolder.bindItem(getItem(position), position)
		return convertview
	}

	fun getPosition(id: Long): Int {
		if (getCount() == 0) return ID_NOT_FOUND
		return (0 until getCount()).firstOrNull { id == getItemId(it) }
				?: ID_NOT_FOUND
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
		return modifyDropDownView(getView(position, convertView, parent))
	}

	protected abstract fun modifyDropDownView(view: View): View

	protected abstract fun createHolder(convertView: View): HOLDER
	protected abstract fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View

	protected fun inflate(parent: ViewGroup, @LayoutRes layoutId: Int): View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}


	private val userActionsOnItems = PublishSubject.create<UserAction>()

	private val holderListeners = CompositeDisposable()

	fun attachListenerToHolder(holder: HOLDER) {
		holderListeners.add(
				holder.interactionEvents()
						.subscribeBy(onNext = { userActionsOnItems.onNext(it) },
								onError = { userActionsOnItems.onError(it) }
						)
		)
	}

	override fun interactionEvents(): Observable<UserAction> {

		return userActionsOnItems.doOnDispose {
			holderListeners.dispose()
		}
	}

	companion object {

		const val ID_NOT_FOUND = -1
	}
}
