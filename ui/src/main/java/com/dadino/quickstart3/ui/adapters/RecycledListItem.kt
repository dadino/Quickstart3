package com.dadino.quickstart3.ui.adapters

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes

abstract class RecycledListItem(metadata: ListItemMetadata = ListItemMetadata()) : ListItem(metadata) {

  fun numericId(): Long = getStringId().hashCode().toLong()

  @LayoutRes
  abstract fun getLayoutId(): Int

  fun canGenerateHolder(viewType: Int): Boolean = viewType == getLayoutId()

  fun generateHolder(getView: (Int) -> View): ListItemHolder {
	return generateHolderForItem(getView(getLayoutId()))
  }

  abstract fun generateHolderForItem(view: View): ListItemHolder

  fun createUpdateBundle(oldItem: ListItem): Bundle? {
	val diff = Bundle()

	createUpdateBundleForItem(diff, oldItem)
	createBaseUpdateBundle(diff, oldItem)

	return if (diff.size() == 0) {
	  null
	} else diff
  }

  protected abstract fun createUpdateBundleForItem(diff: Bundle, oldItem: ListItem)
  private fun createBaseUpdateBundle(diff: Bundle, oldItem: ListItem) {
	if (this.selected != oldItem.selected || this.selectionType != oldItem.selectionType) {
	  diff.putString(PAYLOAD_SELECTED, PAYLOAD_SELECTED)
	}
	if (this.focused != oldItem.focused) {
	  diff.putString(PAYLOAD_FOCUSED, PAYLOAD_FOCUSED)
	}
	if (this.showInCard != oldItem.showInCard || this.indent != oldItem.indent) {
	  diff.putString(PAYLOAD_CARD, PAYLOAD_CARD)
	}
	if (this.spanSizeRes != oldItem.spanSizeRes) {
	  diff.putString(PAYLOAD_SPAN_SIZE, PAYLOAD_SPAN_SIZE)
	}
  }

  fun isContentTheSame(oldItem: ListItem): Boolean {
	return this.selected == oldItem.selected
		&& this.selectionType == oldItem.selectionType
		&& this.showInCard == oldItem.showInCard
		&& this.spanSizeRes == oldItem.spanSizeRes
		&& this.indent == oldItem.indent
		&& this.focused == oldItem.focused
		&& this == oldItem
  }
}