package com.dadino.quickstart3.core.entities

import io.reactivex.Observable

interface UserActionable {
	fun userActions(): Observable<UserAction>
}