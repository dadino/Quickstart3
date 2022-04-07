package com.dadino.quickstart3.sample.viewmodels.flow

import com.dadino.quickstart3.action.Action


interface StateWithActions {

	fun getActionModeActions(): List<Action> = listOf()
	fun getFabAction(): Action? = null
	fun getToolbarActions(): List<Action> = listOf()
}
