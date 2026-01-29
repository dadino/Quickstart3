package com.dadino.quickstart3.ui.adapters

import android.os.Parcelable
import androidx.annotation.IntegerRes
import com.dadino.quickstart3.ui.utils.Indent
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListItemMetadata(
  val focused: Boolean = false,
  val selected: Boolean = false,
  val selectionType: SelectionType = SelectionType.NoSelection,
  val showInCard: Boolean = false,
  val indent: Indent = Indent(0),
  @IntegerRes val spanSizeRes: Int? = null
) : Parcelable {

  class Builder(metadata: ListItemMetadata = ListItemMetadata()) {
	var focused = metadata.focused
	var selected = metadata.selected
	var selectionType = metadata.selectionType
	var showInCard = metadata.showInCard
	var indent = metadata.indent
	var spanSizeRes = metadata.spanSizeRes

	fun build() = ListItemMetadata(
	  focused, selected, selectionType, showInCard, indent, spanSizeRes
	)
  }
}

inline fun metadata(block: ListItemMetadata.Builder.() -> Unit): ListItemMetadata {
  val builder = ListItemMetadata.Builder()
  builder.block()
  return builder.build()
}