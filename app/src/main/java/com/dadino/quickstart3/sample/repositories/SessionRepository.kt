package com.dadino.quickstart3.sample.repositories

import android.util.Log
import com.dadino.quickstart3.core.utils.toAsync
import com.dadino.quickstart3.sample.entities.Session
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single


interface ISessionRepository {
	fun getCurrentSession(): Flowable<Session>
	fun saveCurrentSession(session: Session): Single<Boolean>
	fun closeCurrentSession(): Single<Boolean>
}

class MemorySessionRepository : ISessionRepository {
	private val relay: BehaviorRelay<Session> by lazy {
		BehaviorRelay.createDefault<Session>(Session())
	}

	private val repositoryFlowable: Flowable<Session>  by lazy {
		relay.doOnNext { Log.d("Session", "----> $it") }
				.toFlowable(BackpressureStrategy.LATEST)
				.retry()
				.toAsync()
				.replayingShare()
	}

	override fun getCurrentSession(): Flowable<Session> {
		return repositoryFlowable
	}

	override fun saveCurrentSession(session: Session): Single<Boolean> {
		relay.accept(session)
		return Single.just(true)
	}

	override fun closeCurrentSession(): Single<Boolean> {
		return saveCurrentSession(Session())
	}
}


