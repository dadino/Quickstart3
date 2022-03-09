package com.dadino.quickstart3.core.entities

import com.dadino.quickstart3.base.Event


class SideEffectNotHandledException(sideEffect: SideEffect) : RuntimeException("SideEffect not handled: ${sideEffect.javaClass.simpleName}")
class ReceivedEventWhileQuickLoopNotConnectedException(event: Event) : RuntimeException("Event ${event.javaClass.simpleName} received by QuickLoop while not connected")