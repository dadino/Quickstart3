package com.dadino.quickstart3.core.utils

import android.text.TextUtils


fun String.isValidEmail(): Boolean {
	return this.isEmpty().not() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isNullOrEmpty(): Boolean {
	return TextUtils.isEmpty(this)
}

