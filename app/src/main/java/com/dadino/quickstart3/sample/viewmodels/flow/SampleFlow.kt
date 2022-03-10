package com.dadino.quickstart3.sample.viewmodels.flow

import com.dadino.quickstart3.flow.Flow
import com.dadino.quickstart3.flow.FlowStep

class SampleFlow<STATE>(root: FlowStep<STATE>, steps: List<SampleFlowStep<STATE>>) : Flow<SampleFlow<STATE>, STATE, SampleFlowStep<STATE>>(root, steps) {
	constructor(root: SampleFlowStep<STATE>) : this(root = root, steps = listOf(root))

	override fun getFlow(): SampleFlow<STATE> {
		return this
	}

	override fun updateFlowWithSteps(steps: List<SampleFlowStep<STATE>>): SampleFlow<STATE> {
		return SampleFlow(root = this.root, steps = steps)
	}
}