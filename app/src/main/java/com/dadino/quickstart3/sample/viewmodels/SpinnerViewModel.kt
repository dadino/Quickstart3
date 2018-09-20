package com.dadino.quickstart3.sample.viewmodels

import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.RxSingleSideEffectHandler
import com.dadino.quickstart3.core.components.SideEffectHandler
import com.dadino.quickstart3.core.components.SingleSideEffectHandler
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Next.Companion.stateAndSignal
import com.dadino.quickstart3.sample.entities.ExampleData
import com.dadino.quickstart3.sample.entities.Session
import com.dadino.quickstart3.sample.repositories.ISessionRepository
import io.reactivex.Flowable

class SpinnerViewModel constructor(private val sessionRepo: ISessionRepository) : BaseViewModel<SpinnerState>() {
	init {
		connect()
	}

	override fun getStart() = Start.start(state = SpinnerState(), effects = listOf(SpinnerEffect.LoadSession))

	override fun updateFunction() = { previous: SpinnerState, event: Event ->
		when (event) {
			is SpinnerEvent.OnSpinnerRetryClicked   -> justEffect(SpinnerEffect.LoadSpinnerEntries)
			is SpinnerEvent.OnSpinnerDoneClicked    -> stateAndSignal(newState = previous.copy(list = event.list, loading = false, error = false), signal = SpinnerSignal.ShowDoneToast)
			is SpinnerEvent.OnSpinnerLoadingClicked -> justState(previous.copy(list = listOf(), loading = true, error = false))
			is SpinnerEvent.OnSpinnerErrorClicked   -> justState(previous.copy(list = listOf(), loading = false, error = true))
			is SpinnerEvent.OnSpinnerIdleClicked    -> justState(previous.copy(list = listOf(), loading = false, error = false))
			is SpinnerEvent.OnExampleDataSelected   -> justState(previous.copy(selectedId = event.item?.id))
			is SpinnerEvent.OnSaveSessionRequested  -> justEffect(SpinnerEffect.SaveSession(event.id))
			is SpinnerEvent.SetSaveSessionCompleted -> justSignal(SpinnerSignal.ShowSaveSessionCompleted)
			is SpinnerEvent.SetLoadSessionCompleted -> stateAndSignal(newState = previous.copy(session = event.session), signal = SpinnerSignal.ShowLoadSessionCompleted(event.session))
			else                                    -> noChanges()
		}
	}

	override fun getSideEffectHandlers(): List<SideEffectHandler> {
		return listOf(LoadSessionSideEffectHandler(sessionRepo),
				SaveSessionSideEffectHandler(sessionRepo),
				LoadSpinnerEntriesSideEffectHandler())
	}
}

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

data class SpinnerState(
		val selectedId: Long? = 0,
		val session: Session? = null,
		val loading: Boolean = false,
		val error: Boolean = false,
		val list: List<ExampleData> = listOf()
) : State()

sealed class SpinnerEffect : SideEffect() {
	object LoadSpinnerEntries : SideEffect()
	object LoadSession : SideEffect()
	class SaveSession(val sessionId: String) : SideEffect()
}

sealed class SpinnerSignal : Signal() {
	object ShowDoneToast : Signal()
	object ShowSaveSessionCompleted : Signal()
	class ShowLoadSessionCompleted(val session: Session) : Signal()
}

sealed class SpinnerEvent : Event() {
	class OnSpinnerRetryClicked : Event()
	class OnSpinnerIdleClicked : Event()
	class OnSpinnerLoadingClicked : Event()
	class OnSpinnerErrorClicked : Event()
	class OnSpinnerDoneClicked(val list: List<ExampleData> = listOf(ExampleData(1, "Mario Rossi"), ExampleData(2, "Ennio Santi"), ExampleData(3, "Gino Rossi"))) : Event()

	class OnExampleDataSelected(val item: ExampleData?) : Event()
	class OnSaveSessionRequested(val id: String) : Event()

	class SetLoadSessionCompleted(val session: Session) : Event()
	class SetLoadSessionInProgress : Event()
	class SetLoadSessionError(val error: Throwable) : Event()

	class SetSaveSessionCompleted(val success: Boolean) : Event()
	class SetSaveSessionInProgress : Event()
	class SetSaveSessionError(val error: Throwable) : Event()
}