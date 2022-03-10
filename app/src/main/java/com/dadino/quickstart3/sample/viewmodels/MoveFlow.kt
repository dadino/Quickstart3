package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.flow.Flow
import com.dadino.quickstart3.flow.FlowStep


class MoveFlow<STATE>(root: FlowStep<STATE>, steps: List<MoveFlowStep<STATE>>) : Flow<STATE, MoveFlowStep<STATE>>(root, steps) {
	constructor(root: MoveFlowStep<STATE>) : this(root = root, steps = listOf(root))
}