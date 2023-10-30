package com.dadino.quickstart3.action

import com.dadino.quickstart3.selectable.Id


class ActionId(id: Int) : Id(TYPE, id.toString()) {
	companion object {

		const val TYPE = "action"
	}
}