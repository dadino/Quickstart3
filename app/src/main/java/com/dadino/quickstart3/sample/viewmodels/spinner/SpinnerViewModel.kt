package com.dadino.quickstart3.sample.viewmodels.spinner

import com.dadino.quickstart3.core.components.*
import com.dadino.quickstart3.core.entities.*
import com.dadino.quickstart3.core.entities.Next.Companion.justEffect
import com.dadino.quickstart3.core.entities.Next.Companion.justSignal
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Next.Companion.stateAndSignal
import com.dadino.quickstart3.sample.entities.ExampleData
import com.dadino.quickstart3.sample.entities.Session
import com.dadino.quickstart3.sample.repositories.ISessionRepository

class SpinnerViewModel constructor(private val sessionRepo: ISessionRepository) : BaseViewModel<SpinnerState>() {
	init {
		enableLogging(true)
	}

	override fun updater(): Updater<SpinnerState> {
		return SpinnerUpdater()
	}

	override fun getSideEffectHandlers(): List<SideEffectHandler> {
		return listOf(LoadSessionSideEffectHandler(sessionRepo),
				SaveSessionSideEffectHandler(sessionRepo),
				LoadSpinnerEntriesSideEffectHandler())
	}
}

data class SpinnerState(
		val selectedId: Long? = 0,
		val session: Session? = null,
		val loading: Boolean = false,
		val error: Boolean = false,
		val list: List<ExampleData> = listOf()
) : State()

class SpinnerUpdater : Updater<SpinnerState>(true) {

	override fun start(): Start<SpinnerState> {
		return Start.start(state = SpinnerState(), effects = listOf(SpinnerEffect.LoadSession))
	}

	override fun update(previous: SpinnerState, event: Event): Next<SpinnerState> {
		return when (event) {
			is SpinnerEvent.OnSpinnerRetryClicked -> justEffect(SpinnerEffect.LoadSpinnerEntries)
			is SpinnerEvent.OnSpinnerDoneClicked -> stateAndSignal(newState = previous.copy(list = event.list, loading = false, error = false), signal = SpinnerSignal.ShowDoneToast)
			is SpinnerEvent.OnSpinnerLoadingClicked -> justState(previous.copy(list = listOf(), loading = true, error = false))
			is SpinnerEvent.OnSpinnerErrorClicked -> justState(previous.copy(list = listOf(), loading = false, error = true))
			is SpinnerEvent.OnSpinnerIdleClicked -> justState(previous.copy(list = listOf(), loading = false, error = false))
			is SpinnerEvent.OnExampleDataSelected   -> justState(previous.copy(selectedId = event.item?.id))
			is SpinnerEvent.OnSaveSessionRequested  -> justEffect(SpinnerEffect.SaveSession(event.id))
			is SpinnerEvent.SetSaveSessionCompleted -> justSignal(SpinnerSignal.ShowSaveSessionCompleted)
			is SpinnerEvent.SetLoadSessionCompleted -> stateAndSignal(newState = previous.copy(session = event.session), signal = SpinnerSignal.ShowLoadSessionCompleted(event.session))
			else                                    -> noChanges()
		}
	}
}