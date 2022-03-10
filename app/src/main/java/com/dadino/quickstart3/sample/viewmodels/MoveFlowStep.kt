package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.action.Action
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.ui.adapters.ListItem

abstract class MoveFlowStep<STATE>(key: String) : com.dadino.quickstart3.flow.FlowStep<STATE>(key) {

	abstract fun getTitle(state: STATE): ContextFormattable
	abstract fun getListItems(state: STATE): List<ListItem>
	open fun getActionModeActions(state: STATE): List<Action> = listOf()
	open fun getFabAction(state: STATE): Action? = null
	open fun getToolbarActions(state: STATE): List<Action> = listOf()

	override fun toString(): String {
		return key
	}
}