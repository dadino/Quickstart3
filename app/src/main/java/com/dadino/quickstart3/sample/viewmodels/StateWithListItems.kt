package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.ui.adapters.ListItem

interface StateWithListItems {

	fun getListItemsForState(): List<ListItem>
}