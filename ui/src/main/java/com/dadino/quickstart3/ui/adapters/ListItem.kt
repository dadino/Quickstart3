package com.dadino.quickstart3.ui.adapters

abstract class ListItem(open val metadata: ListItemMetadata = ListItemMetadata()) {
  val focused get() = metadata.focused
  val selected get() = metadata.selected
  val selectionType get() = metadata.selectionType
  val showInCard get() = metadata.showInCard
  val indent get() = metadata.indent
  val spanSizeRes get() = metadata.spanSizeRes

  abstract fun getStringId(): String
  open fun reportWhenNotVisible(): Boolean = false

  abstract fun copyWithMetadata(metadata: ListItemMetadata): ListItem

  companion object {

	const val PAYLOAD_SELECTED = "PAYLOAD_SELECTED"
	const val PAYLOAD_FOCUSED = "PAYLOAD_FOCUSED"
	const val PAYLOAD_CARD = "PAYLOAD_CARD"
	const val PAYLOAD_SPAN_SIZE = "PAYLOAD_SPAN_SIZE"
  }
}

@Suppress("UNCHECKED_CAST")
inline fun <T : ListItem> T.mutate(block: ListItemMetadata.Builder.() -> Unit): T {
  val builder = ListItemMetadata.Builder(this.metadata)
  builder.block()
  return this.copyWithMetadata(builder.build()) as T
}