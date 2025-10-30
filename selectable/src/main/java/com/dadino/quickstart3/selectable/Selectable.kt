package com.dadino.quickstart3.selectable

import android.os.Parcelable
import com.dadino.quickstart3.color.ContextColor
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.icon.ContextDrawable
import kotlinx.parcelize.Parcelize

interface Selectable : Parcelable {

  fun getSelectionId(): Id
  fun getMainText(): ContextFormattable
  fun getSecondaryText(): ContextFormattable? = null
  fun getSelectionIcon(): ContextDrawable? = null
  fun getSelectionColor(): ContextColor? = null

  fun getSelectionEnabled(): Boolean = true
}

@Parcelize
data class SimpleSelectable(
  private val id: Id,
  private val mainText: ContextFormattable,
  private val secondaryText: ContextFormattable? = null,
  private val icon: ContextDrawable? = null,
  private val color: ContextColor? = null,
  private val enabled: Boolean = true
) : Selectable {

  override fun getSelectionId(): Id {
    return id
  }

  override fun getMainText(): ContextFormattable {
    return mainText
  }

  override fun getSecondaryText(): ContextFormattable? {
    return secondaryText
  }

  override fun getSelectionIcon(): ContextDrawable? {
    return icon
  }

  override fun getSelectionEnabled(): Boolean {
    return enabled
  }

  override fun getSelectionColor(): ContextColor? {
    return color
  }
}

fun <T : Selectable> List<T>?.find(id: Id?): T? {
  if (this == null) return null
  if (id == null) return null
  return this.firstOrNull { it.getSelectionId() == id }
}