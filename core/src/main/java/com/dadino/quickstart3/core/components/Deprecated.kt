package com.dadino.quickstart3.core.components

@Deprecated(message = "Use ContextFormattable from the Quickstart3.contextformattable library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("ContextFormattable", imports = ["com.dadino.quickstart3.contextformattable.ContextFormattable"]))
sealed class ContextFormattable

@Deprecated(message = "Use StringFormattable from the Quickstart3.contextformattable library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("StringFormattable", imports = ["com.dadino.quickstart3.contextformattable.StringFormattable"]))
sealed class StringFormattable

@Deprecated(message = "Use HtmlFormattable from the Quickstart3.contextformattable library", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("HtmlFormattable", imports = ["com.dadino.quickstart3.contextformattable.HtmlFormattable"]))
sealed class HtmlFormattable
