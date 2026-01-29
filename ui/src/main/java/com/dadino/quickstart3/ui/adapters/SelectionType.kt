package com.dadino.quickstart3.ui.adapters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SelectionType(val id: Int) : Parcelable {
  data object NoSelection : SelectionType(0)
  data object SingleSelection : SelectionType(1)
  data object MultipleSelection : SelectionType(2)
}