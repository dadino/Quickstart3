package com.dadino.quickstart3.selectable

abstract class Id(private val type: String, val identifier: String) {
	class Generic(id: String) : Id(TYPE, id) {
		companion object {

			const val TYPE = "generic"
		}
	}

	override fun toString(): String {
		return "$type:$identifier"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Id

		if (type != other.type) return false
		if (identifier != other.identifier) return false

		return true
	}

	override fun hashCode(): Int {
		var result = type.hashCode()
		result = 31 * result + identifier.hashCode()
		return result
	}
}

fun String?.toId(): Id.Generic? = if (this == null) null else Id.Generic(this)