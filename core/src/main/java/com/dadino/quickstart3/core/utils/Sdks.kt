package com.dadino.quickstart3.core.utils


object Sdks {
	fun isAtLeast(minimumVersion: Int): Boolean {
		return android.os.Build.VERSION.SDK_INT >= minimumVersion
	}

	fun isLessThan(minimumVersion: Int): Boolean {
		return android.os.Build.VERSION.SDK_INT < minimumVersion
	}

	fun isLessThan21(): Boolean {
		return isLessThan(android.os.Build.VERSION_CODES.LOLLIPOP)
	}

	fun isLessThan26(): Boolean {
		return isLessThan(android.os.Build.VERSION_CODES.O)
	}
}