package com.dadino.quickstart3.sample.viewmodels.spinner

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.core.components.RxSingleSideEffectHandler
import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.sample.entities.Session
import com.dadino.quickstart3.sample.repositories.ISessionRepository
import io.reactivex.Flowable


class SaveSessionSideEffectHandler(private val sessionRepo: ISessionRepository) : RxSingleSideEffectHandler<SpinnerEffect.SaveSession>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is SpinnerEffect.SaveSession
	}

	override fun effectToFlowable(effect: SpinnerEffect.SaveSession): Flowable<Event> {
		return sessionRepo.saveCurrentSession(Session(effect.sessionId))
			.toFlowable()
			.map<Event> { SpinnerEvent.SetSaveSessionCompleted(it) }
			.startWith(SpinnerEvent.SetSaveSessionInProgress())
			.onErrorReturn { SpinnerEvent.SetSaveSessionError(it) }
	}
}

class LoadSessionSideEffectHandler(private val sessionRepo: ISessionRepository) : RxSingleSideEffectHandler<SpinnerEffect.LoadSession>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is SpinnerEffect.LoadSession
	}

	override fun effectToFlowable(effect: SpinnerEffect.LoadSession): Flowable<Event> {
		return sessionRepo.getCurrentSession()
			.map<Event> { SpinnerEvent.SetLoadSessionCompleted(it) }
			.startWith(SpinnerEvent.SetLoadSessionInProgress())
			.onErrorReturn { SpinnerEvent.SetLoadSessionError(it) }
	}
}

class LoadSpinnerEntriesSideEffectHandler : SingleSideEffectHandler<SpinnerEffect.LoadSpinnerEntries>() {
	override fun checkClass(effect: SideEffect): Boolean {
		return effect is SpinnerEffect.LoadSpinnerEntries
	}

	override fun effectToEvent(effect: SpinnerEffect.LoadSpinnerEntries): Event {
		return SpinnerEvent.OnSpinnerDoneClicked()
	}
}
