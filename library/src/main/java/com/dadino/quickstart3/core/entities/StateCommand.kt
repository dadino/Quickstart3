package com.dadino.quickstart3.core.entities

open class StateCommand

@Deprecated(replaceWith = ReplaceWith("StateCommand"), message = "Replace with StateCommand")
open class ModelCommand : StateCommand()

class NoOpCommand : StateCommand()
