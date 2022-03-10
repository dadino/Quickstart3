package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.flow.Flow
import com.dadino.quickstart3.flow.FlowStep


class MoveFlow<STATE>(root: FlowStep<STATE>, steps: List<MoveFlowStep<STATE>>) : Flow<MoveFlow<STATE>, STATE, MoveFlowStep<STATE>>(root, steps) {
	constructor(root: MoveFlowStep<STATE>) : this(root = root, steps = listOf(root))

	override fun getFlow(): MoveFlow<STATE> {
		return this
	}

	override fun updateFlowWithSteps(steps: List<MoveFlowStep<STATE>>): MoveFlow<STATE> {
		return MoveFlow(root = this.root, steps = steps)
	}
}