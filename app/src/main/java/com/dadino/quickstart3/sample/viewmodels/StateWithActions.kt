package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.action.Action


interface StateWithActions {

	fun getActionModeActions(): List<Action> = listOf()
	fun getFabAction(): Action? = null
	fun getToolbarActions(): List<Action> = listOf()
}
