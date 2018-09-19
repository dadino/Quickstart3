package com.dadino.quickstart3.core.adapters

import com.dadino.quickstart3.core.adapters.holders.BaseHolder

abstract class BaseWrappedListAdapter<WRAPPER, ITEM, HOLDER : BaseHolder<ITEM>> : BaseListAdapter<ITEM, HOLDER>() {

	var wrapper: WRAPPER? = null
		private set

	fun setItem(item: WRAPPER?) {
		this.wrapper = item
		if (item != null) items = getWrappedItems(item)
	}

	protected abstract fun getWrappedItems(wrapper: WRAPPER): List<ITEM>
}
