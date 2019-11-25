package com.dadino.quickstart3.core.utils

import android.content.res.Resources

class ResNamer(val resources: Resources) {
	fun getDisplayName(id: Int?): String {
		if (id == null) return "null"
		return try {
			resources.getResourceName(id)
		} catch (e: Resources.NotFoundException) {
			id.toString()
		}
	}
}