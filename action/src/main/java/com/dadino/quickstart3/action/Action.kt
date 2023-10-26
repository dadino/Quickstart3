package com.dadino.quickstart3.action

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.contextformattable.asFormattable
import com.dadino.quickstart3.icon.Icon
import com.dadino.quickstart3.icon.asIcon
import com.dadino.quickstart3.selectable.Id
import com.dadino.quickstart3.selectable.Selectable


open class Action(
	val id: Int,
	val text: ContextFormattable? = null,
	val icon: Icon? = null,
	val enabled: Boolean = true,
	val showAsAction: Int = MenuItem.SHOW_AS_ACTION_IF_ROOM,
	val eventOnClick: Event
) : Selectable {
	constructor(
		id: Int,
		text: ContextFormattable? = null,
		@DrawableRes icon: Int? = null,
		enabled: Boolean = true,
		showAsAction: Int = MenuItem.SHOW_AS_ACTION_IF_ROOM,
		eventOnClick: Event
	) : this(
		id = id,
		text = text,
		icon = icon?.asIcon(),
		enabled = enabled,
		showAsAction = showAsAction,
		eventOnClick = eventOnClick
	)

	fun toMenuItem(context: Context, menu: Menu, order: Int): MenuItem {
		val item = menu.add(0, id, order, text?.format(context) ?: "")
		icon?.let { item.setIcon(icon.icon) }
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

	override fun getSelectionId(): Id {
		return ActionId(id)
	}

	override fun getMainText(): ContextFormattable {
		return text ?: "".asFormattable()
	}

	override fun getSecondaryText(): ContextFormattable? {
		return null
	}

	override fun getSelectionIcon(): Icon? {
		return icon
	}
}