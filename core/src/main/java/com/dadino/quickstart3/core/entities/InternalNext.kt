package com.dadino.quickstart3.core.entities

class InternalNext(val states: List<State<*>>,
				   val signals: List<Signal>,
				   val effects: List<SideEffect>,
				   val isStartingState: Boolean)