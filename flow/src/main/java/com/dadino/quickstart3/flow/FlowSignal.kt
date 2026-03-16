package com.dadino.quickstart3.flow

import com.dadino.quickstart3.core.entities.Signal

data class PublishFlowChange<STATE>(val updatedFlowSteps: List<String>, val advancement: FlowAdvancement<STATE>?) : Signal()