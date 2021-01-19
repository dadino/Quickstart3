package com.dadino.quickstart3.ui.adapters

import androidx.recyclerview.widget.DiffUtil

class GenericDiffUtils(private val oldList: List<ListItem>, private val newList: List<ListItem>) :
		DiffUtil.Callback() {

	override fun getOldListSize(): Int {
		return oldList.size
	}

	override fun getNewListSize(): Int {
		return newList.size
	}

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].numericId() == newList[newItemPosition].numericId()
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return newList[newItemPosition].isContentTheSame(oldList[oldItemPosition])
	}

	override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
		return newList[newItemPosition].createUpdateBundle(oldList[oldItemPosition])
	}
}