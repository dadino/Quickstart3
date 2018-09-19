package com.dadino.quickstart3.core.repositories

import android.content.SharedPreferences
import com.dadino.quickstart3.core.interfaces.IRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

abstract class PrefLongRepository(prefs: SharedPreferences) : PrefRepository(prefs), ILongRepository {

	private var subject: BehaviorRelay<Long> = BehaviorRelay.create()

	override protected fun listenOn(): String {
		return key
	}

	override protected fun onPrefChanged() {
		subject.accept(pref)
	}

	override fun retrieve(): Flowable<Long> {
		if (subject.hasValue().not()) {
			subject.accept(pref)
		}
		return subject.toFlowable(BackpressureStrategy.LATEST)
	}

	override fun create(long: Long): Single<Boolean> {
		return Single.just(editor().putLong(key, long)
				.commit())
	}

	override fun delete(): Single<Boolean> {
		return Single.just(editor().remove(key)
				.commit())
	}

	override fun update(long: Long): Single<Boolean> {
		return create(long)
	}

	private val pref: Long
		get() = pref().getLong(key, default)

	protected abstract val default: Long
}

interface ILongRepository : IRepository {

	fun retrieve(): Flowable<Long>
	fun create(long: Long): Single<Boolean>
	fun delete(): Single<Boolean>
	fun update(long: Long): Single<Boolean>
}
