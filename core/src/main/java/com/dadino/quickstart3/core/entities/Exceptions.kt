package com.dadino.quickstart3.core.entities


class SideEffectNotHandledException(sideEffect: SideEffect) : RuntimeException("SideEffect not handled: ${sideEffect.javaClass.simpleName}")