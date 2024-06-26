package com.dadino.quickstart3.core

import com.dadino.quickstart3.base.Event
import org.mockito.Mockito

object TestUtils {

  fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
  const val MAX_WAIT_TIME_FOR_OBSERVABLES = 5000L

  fun testUpdater(): TestStateUpdater {
	val updater = Mockito.mock(TestStateUpdater::class.java)
	Mockito.`when`(updater.start()).thenCallRealMethod()
	Mockito.`when`(updater.update(any(TestState::class.java), any(Event::class.java))).thenCallRealMethod()
	Mockito.`when`(updater.internalUpdate(any(TestState::class.java), any(Event::class.java))).thenCallRealMethod()
	Mockito.`when`(updater.getInitialMainState()).thenCallRealMethod()
	Mockito.`when`(updater.getInitialSubStates()).thenCallRealMethod()
	Mockito.`when`(updater.getStatesToPropagate(any(TestState::class.java), any(TestState::class.java), any(Boolean::class.java))).thenCallRealMethod()
	return updater
  }
}