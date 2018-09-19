package com.dadino.quickstart3.core.sample.entities

import com.dadino.quickstart3.core.entities.UserAction


class OnSpinnerRetryClicked : UserAction()
class OnSpinnerIdleClicked : UserAction()
class OnSpinnerLoadingClicked : UserAction()
class OnSpinnerErrorClicked : UserAction()
class OnSpinnerDoneClicked : UserAction()
class OnGoToSecondPageClicked : UserAction()
class OnExampleDataSelected(val item: ExampleData?) : UserAction()
class OnSaveSessionRequested(val id: String) : UserAction()

class OnAdvanceCounterClicked : UserAction()
class OnShowCounterStateClicked : UserAction()