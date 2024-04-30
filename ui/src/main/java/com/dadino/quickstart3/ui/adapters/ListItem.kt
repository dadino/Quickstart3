package com.dadino.quickstart3.ui.adapters

import android.os.Bundle
import android.view.View
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes

abstract class ListItem {

  var selected: Boolean = false
  var selectionType: SelectionType = SelectionType.NoSelection
  var showInCard: Boolean = false
  var indent: Int = 0

  @IntegerRes
  var spanSizeRes: Int? = null

  abstract fun numericId(): Long

  @LayoutRes
  abstract fun getLayoutId(): Int

  fun createUpdateBundle(oldItem: ListItem): Bundle? {
	val diff = Bundle()

	createUpdateBundleForItem(diff, oldItem)
	createBaseUpdateBundle(diff, oldItem)

	return if (diff.size() == 0) {
	  null
	} else diff
  }

  fun canGenerateHolder(viewType: Int): Boolean = viewType == getLayoutId()

  fun generateHolder(getView: (Int) -> View): ListItemHolder {
	return generateHolderForItem(getView(getLayoutId()))
  }

  abstract fun generateHolderForItem(view: View): ListItemHolder

  protected abstract fun createUpdateBundleForItem(diff: Bundle, oldItem: ListItem)
  private fun createBaseUpdateBundle(diff: Bundle, oldItem: ListItem) {
	if (this.selected != oldItem.selected) {
	  diff.putBoolean(PAYLOAD_SELECTED, this.selected)
	}
	if (this.selectionType != oldItem.selectionType) {
	  diff.putInt(PAYLOAD_SELECTABLE, this.selectionType.id)
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
		&& this == oldItem
  }

  companion object {

	const val PAYLOAD_SELECTED = "PAYLOAD_SELECTED"
	const val PAYLOAD_SELECTABLE = "PAYLOAD_SELECTABLE"
	const val PAYLOAD_CARD = "PAYLOAD_CARD"
	const val PAYLOAD_SPAN_SIZE = "PAYLOAD_SPAN_SIZE"
  }
}

