package com.dadino.quickstart3.sample.viewmodels.spinner

import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.core.entities.SideEffect
import com.dadino.quickstart3.core.entities.Signal
import com.dadino.quickstart3.sample.entities.ExampleData
import com.dadino.quickstart3.sample.entities.Session

sealed class SpinnerEffect : SideEffect() {
	object LoadSpinnerEntries : SideEffect()
	object LoadSession : SideEffect()
	class SaveSession(val sessionId: String) : SideEffect()
}

sealed class SpinnerSignal : Signal() {
	object ShowDoneToast : Signal()
	object ShowSaveSessionCompleted : Signal()
	class ShowLoadSessionCompleted(val session: Session) : Signal()
	object OpenSecondActivity : Signal()
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