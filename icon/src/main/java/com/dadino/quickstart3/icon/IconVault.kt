package com.dadino.quickstart3.icon

import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

object IconVault {
	private val iconMap: HashMap<String, Icon> = hashMapOf()
	fun getIcon(
		@DrawableRes icon: Int,
		@ColorRes tint: Int? = null,
		@AnimRes animation: Int? = null
	): Icon {
		val id = getIconId(icon, tint, animation)
		val oldIcon = iconMap[id]
		return if (oldIcon != null) oldIcon
		else {
			val newIcon = Icon(icon, tint, animation)
			iconMap[id] = newIcon
			newIcon
		}
	}

	private fun getIconId(
		@DrawableRes icon: Int,
		@ColorRes tint: Int?,
		@AnimRes animation: Int?
	): String = "$icon:$tint:$animation"
}