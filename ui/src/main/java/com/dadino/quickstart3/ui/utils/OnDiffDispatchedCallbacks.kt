package com.dadino.quickstart3.ui.utils

interface OnDiffDispatchedCallbacks {
  fun onDiffBegin()
  fun onDiffDispatched(itemCount: Int, executionTimeInMillis: Long)
  fun onDiffError(executionTimeInMillis: Long)
}