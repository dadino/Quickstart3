package com.dadino.quickstart3.sample.listitems

import android.os.Bundle
import android.view.View
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.ui.adapters.ListItem


data class GridCell(
	val id: String,
	val message: ContextFormattable? = null,
	val onMinusClick: Event,
	val onPlusClick: Event,
) : ListItem() {

	override fun numericId(): Long {
		return "gridCell:$id".hashCode().toLong()
	}

	override fun getLayoutId() = GridCellHolder.layoutId

	override fun createUpdateBundleForItem(diff: Bundle, oldItem: ListItem) {
		if (oldItem !is GridCell) return
		if (this.message != oldItem.message) {
			diff.putString(GridCellHolder.PAYLOAD_MESSAGE, "icon")
		}
	}

	override fun generateHolderForItem(view: View) = GridCellHolder(view)
}