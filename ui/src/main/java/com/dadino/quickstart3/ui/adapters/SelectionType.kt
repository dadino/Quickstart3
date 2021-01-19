package com.dadino.quickstart3.ui.adapters

sealed class SelectionType(val id: Int) {
	object NoSelection : SelectionType(0)
	object SingleSelection : SelectionType(1)
	object MultipleSelection : SelectionType(2)
}