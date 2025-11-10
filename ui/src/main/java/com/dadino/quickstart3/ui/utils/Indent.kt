package com.dadino.quickstart3.ui.utils

import androidx.compose.runtime.Stable

@Stable
data class Indent(
  val indent: Int,
  var hasFollowingChild: Boolean = false,
  var hasFollowingSibling: Boolean = false,
  var hasPrecedingSibling: Boolean = false,
  var isFirstChild: Boolean = false
)
