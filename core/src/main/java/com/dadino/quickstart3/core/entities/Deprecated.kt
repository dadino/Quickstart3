package com.dadino.quickstart3.core.entities

@Deprecated(message = "Use Event from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("Event", imports = ["com.dadino.quickstart3.base.Event"]))
open class Event

@Deprecated(message = "Use InitializeState from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("InitializeState", imports = ["com.dadino.quickstart3.base.InitializeState"]))
object InitializeState

@Deprecated(message = "Use NoOpEvent from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("NoOpEvent", imports = ["com.dadino.quickstart3.base.NoOpEvent"]))
object NoOpEvent

@Deprecated(message = "Use LifecycleEvent from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("LifecycleEvent", imports = ["com.dadino.quickstart3.base.LifecycleEvent"]))
sealed class LifecycleEvent

@Deprecated(message = "Use Operation from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("Operation", imports = ["com.dadino.quickstart3.base.Operation"]))
sealed class Operation

@Deprecated(message = "Use Optional from the Quickstart3.base library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("Optional", imports = ["com.dadino.quickstart3.base.Optional"]))
sealed class Optional
