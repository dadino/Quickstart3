package com.dadino.quickstart3.core.repositories

import android.content.SharedPreferences
import com.dadino.quickstart3.core.components.IRepository

abstract class PrefRepository(private val prefs: SharedPreferences) : IRepository, SharedPreferences.OnSharedPreferenceChangeListener {

	init {
		prefs.registerOnSharedPreferenceChangeListener(this)
	}

	override fun onDestroy() {
		prefs.unregisterOnSharedPreferenceChangeListener(this)
	}

	protected abstract fun listenOn(): String

	protected fun pref(): SharedPreferences {
		return prefs
	}

	protected fun editor(): SharedPreferences.Editor {
		return prefs.edit()
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
		if (listenOn() == s) onPrefChanged()
	}

	protected abstract fun onPrefChanged()

	protected abstract val key: String
}