package com.dadino.quickstart3.sample.entities

import com.dadino.quickstart3.core.entities.StateCommand


class SetInProgress : StateCommand()
class SetIdle : StateCommand()
class SetError : StateCommand()
class SetDone(val list: List<ExampleData>) : StateCommand()
class SetItemSelected(val selectedId: Long?) : StateCommand()

class SetLoadSessionCompleted(val session: Session) : StateCommand()
class SetLoadSessionInProgress : StateCommand()
class SetLoadSessionError(val error: Throwable) : StateCommand()

class AddToCounter : StateCommand()