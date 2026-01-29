package com.dadino.quickstart3.ui.utils

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class Indent(
  val indent: Int,
  var hasFollowingChild: Boolean = false,
  var hasFollowingSibling: Boolean = false,
  var hasPrecedingSibling: Boolean = false,
  var isFirstChild: Boolean = false
) : Parcelable
