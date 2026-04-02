package com.dadino.quickstart3.sample.viewmodels.flow

import com.dadino.quickstart3.flow.FlowState
import com.dadino.quickstart3.flow.FlowWithAdvancements

class SampleFlow<STATE : FlowState<*, STATE, SampleFlowStep<STATE>>>(root: SampleFlowStep<STATE>, steps: List<SampleFlowStep<STATE>>) :
  FlowWithAdvancements<SampleFlow<STATE>, STATE, SampleFlowStep<STATE>>(root, steps) {
	constructor(root: SampleFlowStep<STATE>) : this(root = root, steps = listOf(root))

	override fun getFlow(): SampleFlow<STATE> {
		return this
	}

	override fun updateFlowWithSteps(steps: List<SampleFlowStep<STATE>>): SampleFlow<STATE> {
		return SampleFlow(root = this.root, steps = steps)
	}
}