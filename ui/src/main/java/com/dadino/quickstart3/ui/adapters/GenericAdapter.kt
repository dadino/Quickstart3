package com.dadino.quickstart3.ui.adapters

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.core.utils.printQuickStackTrace

class GenericAdapter(val log: Boolean = false) : BaseListAdapter<ListItem, ListItemHolder>() {
  private var itemsToReportWhenNotVisible: Map<Int, ListItem> = emptyMap()

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
		  QuickLogger.tag("GenericAdapter").d { "Updating holder ${holder::class.java.simpleName} at position $position with bundle $bundle" }
		}
	  } catch (e: Exception) {
		e.printQuickStackTrace("GenericAdapter")
	  }
	  holder.updateWithPayloads(getItem(position)!!, payloads)
	} else {
	  if (log) QuickLogger.tag("GenericAdapter").d { "Binding holder ${holder::class.java.simpleName} at position $position" }
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

  override fun touchItemListBeforeSend(itemList: List<ListItem>): List<ListItem> {
	itemsToReportWhenNotVisible = itemList.mapIndexedNotNull { index, listItem ->
	  if (listItem.reportWhenNotVisible())
		index to listItem
	  else null
	}.toMap()
	QuickLogger.tag("GenericAdapter").d { "itemsToReportWhenNotVisible: ${itemsToReportWhenNotVisible.entries.joinToString(", ") { "(${it.key} - ${it.value::class.java.simpleName})" }}" }

	if (itemList.all { it.indent.indent == 0 }) return itemList
	for (i in itemList.indices) {
	  val hasPrecedingSibling = i > 0 && itemList[i - 1].indent.indent == itemList[i].indent.indent
	  val hasFollowingSibling = i < itemList.size - 1 && itemList[i + 1].indent.indent == itemList[i].indent.indent
	  val hasFollowingChild = i < itemList.size - 1 && itemList[i + 1].indent.indent > itemList[i].indent.indent
	  val isFirstChild = i == 0 || itemList[i - 1].indent.indent < itemList[i].indent.indent
	  itemList[i].indent = itemList[i].indent.copy(
		hasPrecedingSibling = hasPrecedingSibling,
		hasFollowingSibling = hasFollowingSibling,
		hasFollowingChild = hasFollowingChild,
		isFirstChild = isFirstChild
	  )
	}

	return itemList
  }

  fun getItemsNotVisible(
	firstVisibleItemPosition: Int,
	lastVisibleItemPosition: Int
  ): Pair<Map<Int, ListItem>, Map<Int, ListItem>> {
	if (itemsToReportWhenNotVisible.isEmpty()) return emptyMap<Int, ListItem>() to emptyMap<Int, ListItem>()
	return if (firstVisibleItemPosition > 0) {
	  itemsToReportWhenNotVisible.filter { (index, _) -> index in 0 until firstVisibleItemPosition }
	} else {
	  emptyMap()
	} to
		if (isLastItem(lastVisibleItemPosition).not()) {
		  itemsToReportWhenNotVisible.filter { (index, _) -> index > lastVisibleItemPosition }
		} else {
		  emptyMap()
		}
  }
}

abstract class ListItemHolder(view: View) : BaseHolder<ListItem>(view)

