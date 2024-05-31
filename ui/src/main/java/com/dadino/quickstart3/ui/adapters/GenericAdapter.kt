package com.dadino.quickstart3.ui.adapters

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil

class GenericAdapter(val log: Boolean = false) : BaseListAdapter<ListItem, ListItemHolder>() {

  override fun getDiffCallbacks(
	  oldList: List<ListItem>,
	  newList: List<ListItem>
  ): DiffUtil.Callback? {
	return GenericDiffUtils(oldList, newList)
  }

  override fun onBindViewHolder(
	  holder: ListItemHolder,
	  position: Int,
	  payloads: MutableList<Any>
  ) {
	if (payloads.isNotEmpty() && holder is UpdatableHolder && getItem(position) != null) {
	  try {
		if (log) {
		  val bundle = payloads.firstOrNull() as? Bundle
		  Log.d("GenericAdapter", "Updating holder ${holder::class.java.simpleName} at position $position with bundle $bundle")
		}
	  } catch (e: Exception) {
		e.printStackTrace()
	  }
	  holder.updateWithPayloads(getItem(position)!!, payloads)
	} else {
	  if (log) Log.d("GenericAdapter", "Binding holder ${holder::class.java.simpleName} at position $position")
	  super.onBindViewHolder(holder, position, payloads)
	}
  }

  override fun getHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
	return items?.firstOrNull { it.canGenerateHolder(viewType) }?.generateHolder { layoutId -> inflate(parent, layoutId) } ?: throw RuntimeException("ListItem not handled")
  }

  override fun getItemIdSafe(position: Int): Long {
	return getItem(position)?.numericId() ?: 0
  }

  override fun getItemViewType(position: Int): Int {
	return getItem(position)?.getLayoutId() ?: throw RuntimeException("ListItem not handled")
  }
}

abstract class ListItemHolder(view: View) : BaseHolder<ListItem>(view)

