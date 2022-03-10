package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.action.Action
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.flow.Flow
import com.dadino.quickstart3.flow.FlowState
import com.dadino.quickstart3.ui.adapters.ListItem

abstract class MoveFlowState<STATE>(
	override val flow: Flow<STATE, MoveFlowStep<STATE>>
) : FlowState<STATE, MoveFlowStep<STATE>>(flow), StateWithFlow {

	override fun getListItemsForState(): List<ListItem> {
		return flow.getCurrentStep()?.getListItems(getState()) ?: listOf()
	}

	override fun getTitle(): ContextFormattable? {
		return flow.getCurrentStep()?.getTitle(getState())
	}

	override fun getActionModeActions(): List<Action> {
		return flow.getCurrentStep()?.getActionModeActions(getState()) ?: listOf()
	}

	override fun getFabAction(): Action? {
		return flow.getCurrentStep()?.getFabAction(getState())
	}

	override fun getToolbarActions(): List<Action> {
		return flow.getCurrentStep()?.getToolbarActions(getState()) ?: listOf()
	}
}
