package com.dadino.quickstart3.icon

import android.content.Context
import android.graphics.drawable.Drawable

object DrawableVault {
  private val iconMap: HashMap<String, Drawable> = hashMapOf()
  fun getDrawable(
	context: Context,
	contextDrawable: ContextDrawable
  ): Drawable? {
	val id = contextDrawable.getVaultId(context)
	val oldIcon = iconMap[id]
	return if (oldIcon != null) oldIcon
	else {
	  val newDrawable = contextDrawable.createDrawable(context)
	  if (newDrawable != null) iconMap[id] = newDrawable
	  newDrawable
	}
  }

  fun describeContent(): String {
	return "DrawableVault contains:\n${iconMap.entries.sortedBy { it.key }.joinToString("\n") { it.key }}"
  }
}