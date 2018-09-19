package com.dadino.quickstart3.core.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager


object Keyboard {

	fun close(activity: Activity?) {
		activity?.currentFocus?.let {
			(activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
					.hideSoftInputFromWindow(it.windowToken, 0)
		}
	}
}