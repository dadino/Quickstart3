package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.flow.FlowUpdater


abstract class MoveFlowUpdater<STATE : MoveFlowState<STATE>>(enableLogging: Boolean) : FlowUpdater<MoveFlow<STATE>, STATE, MoveFlowStep<STATE>>(enableLogging)