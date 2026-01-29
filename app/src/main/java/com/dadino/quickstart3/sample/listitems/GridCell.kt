package com.dadino.quickstart3.sample.listitems

import android.os.Bundle
import android.view.View
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.ui.adapters.ListItem
import com.dadino.quickstart3.ui.adapters.ListItemMetadata
import com.dadino.quickstart3.ui.adapters.RecycledListItem

data class GridCell(
  val id: String,
  val message: ContextFormattable? = null,
  val onMinusClick: Event,
  val onPlusClick: Event,
  override val metadata: ListItemMetadata = ListItemMetadata()
) : RecycledListItem(metadata) {

  override fun getStringId(): String {
	return "gridCell:$id"
  }

  override fun getLayoutId() = GridCellHolder.layoutId

  override fun createUpdateBundleForItem(diff: Bundle, oldItem: ListItem) {
	if (oldItem !is GridCell) return
	if (this.message != oldItem.message) {
	  diff.putString(GridCellHolder.PAYLOAD_MESSAGE, "icon")
	}
  }

  override fun generateHolderForItem(view: View) = GridCellHolder(view)
  override fun copyWithMetadata(metadata: ListItemMetadata): ListItem = this.copy(metadata = metadata)
}