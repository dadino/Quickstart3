package com.dadino.quickstart3.sample.viewmodels.flow

import com.dadino.quickstart3.flow.FlowUpdater

abstract class SampleFlowUpdater<STATE : SampleFlowState<STATE>>(enableLogging: Boolean) : FlowUpdater<SampleFlow<STATE>, STATE, SampleFlowStep<STATE>>(enableLogging)