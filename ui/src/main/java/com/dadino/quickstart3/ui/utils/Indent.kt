package com.dadino.quickstart3.ui.utils

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class Indent(
  val indent: Int,
  @Deprecated("Not used anymore") val hasFollowingChild: Boolean = false,
  val hasFollowingSibling: Boolean = false,
  @Deprecated("Not used anymore") val hasPrecedingSibling: Boolean = false,
  val isFirstChild: Boolean = false,
  val activeParentLines: List<Int> = emptyList()
) : Parcelable
