package com.dadino.quickstart3.sample.viewmodels.flow

import com.dadino.quickstart3.ui.adapters.ListItem

interface StateWithListItems {

	fun getListItemsForState(): List<ListItem>
}