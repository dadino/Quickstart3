package com.dadino.quickstart3.core.adapters

import com.dadino.quickstart3.core.adapters.holders.BaseHolder

abstract class BaseSingleItemAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : BaseAdapter<ITEM, HOLDER>() {

	protected var layoutInflater: android.view.LayoutInflater? = null
	var item: ITEM? = null
		set(item) {
			field = item
			notifyDataSetChanged()
		}

	init {
		setHasStableIds(useStableId())
	}

	protected fun useStableId(): Boolean {
		return false
	}

	protected fun inflater(context: android.content.Context): android.view.LayoutInflater {
		if (layoutInflater == null) layoutInflater = android.view.LayoutInflater.from(context)
		return layoutInflater!!
	}

	override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HOLDER {
		val holder = getHolder(parent, viewType)
		attachListenerToHolder(holder)
		return holder
	}

	override fun onBindViewHolder(holder: HOLDER, position: Int) {
		item?.let { bindItem(holder, it, position) }
	}

	override abstract fun getItemId(position: Int): Long

	fun isLastItem(position: Int): Boolean {
		return position == itemCount - 1
	}

	fun bindItem(holder: HOLDER, item: ITEM, position: Int) {
		holder.bindItem(item, position)
	}

	protected fun inflate(parent: android.view.ViewGroup, @androidx.annotation.LayoutRes layoutId: Int): android.view.View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	protected abstract fun getHolder(parent: android.view.ViewGroup, viewType: Int): HOLDER
}
