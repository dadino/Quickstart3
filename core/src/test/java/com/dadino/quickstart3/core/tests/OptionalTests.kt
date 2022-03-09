package com.dadino.quickstart3.core.tests

import com.dadino.quickstart3.base.Optional
import org.junit.Assert.*
import org.junit.Test


class OptionalTests {
	@Test
	fun offerNotNull_createSome() {
		//GIVEN
		val stuff = "not null string"

		//WHEN
		val optional = Optional.create(stuff)

		//THEN
		assertTrue(optional is Optional.Some)
	}

	@Test
	fun offerNull_createNone() {
		//GIVEN
		val stuff = null

		//WHEN
		val optional = Optional.create(stuff)

		//THEN
		assertTrue(optional is Optional.None)
	}

	@Test
	fun requestFromSome_receiveNotNull() {
		//GIVEN
		val optional = Optional.create("not null string")

		//WHEN
		val stuff = optional.element()

		//THEN
		assertNotNull(stuff)
	}

	@Test
	fun requestFromNone_receiveNull() {
		//GIVEN
		val optional = Optional.create(null)

		//WHEN
		val stuff = optional.element()

		//THEN
		assertNull(stuff)
	}
}