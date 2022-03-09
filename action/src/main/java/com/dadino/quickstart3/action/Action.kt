package com.dadino.quickstart3.action

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.ContextFormattable


open class Action(
	val id: Int,
	val text: ContextFormattable? = null,
	@DrawableRes val icon: Int? = null,
	val enabled: Boolean = true,
	val showAsAction: Int = MenuItem.SHOW_AS_ACTION_IF_ROOM,
	val eventOnClick: Event
) {

	fun toMenuItem(context: Context, menu: Menu, order: Int): MenuItem {
		val item = menu.add(0, id, order, text?.format(context) ?: "")
		icon?.let { item.setIcon(icon) }
		item.isEnabled = enabled
		item.setShowAsAction(showAsAction)
		return item
	}

	companion object {

		private fun equalLists(oldList: List<Action>?, newList: List<Action>?): Boolean {
			if (oldList == null && newList != null) return false
			else if (oldList != null && newList == null) return false
			else if (oldList == null && newList == null) return true
			else if (oldList != null && newList != null) {
				if (oldList.size != newList.size) {
					return false
				}

				oldList.forEachIndexed { index, value ->
					if (newList[index] != value) {
						return false
					}
				}
				return true
			}
			return false
		}

		fun areDifferentActions(oldList: List<Action>?, newList: List<Action>?): Boolean {
			return equalLists(oldList, newList).not()
		}
	}
}