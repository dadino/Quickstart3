package com.dadino.quickstart3.ui.utils

data class Indent(
  val indent: Int,
  var hasFollowingChild: Boolean = false,
  var hasFollowingSibling: Boolean = false,
  var hasPrecedingSibling: Boolean = false,
  var isFirstChild: Boolean = false
)
