package com.dadino.quickstart3.action

import com.dadino.quickstart3.selectable.Id
import kotlinx.parcelize.Parcelize

@Parcelize
class ActionId(val id: Int) : Id(TYPE, id.toString()) {
  companion object {

	const val TYPE = "action"
  }
}