package com.dadino.quickstart3.core.components

import com.dadino.quickstart3.core.entities.State

interface ViewModelDecorator<STATE : State> {
  fun provideAdditionalSideEffectHandlers(): List<SideEffectHandler>
  fun getUpdaterDecorators(): List<UpdaterDecorator<STATE>>
}
