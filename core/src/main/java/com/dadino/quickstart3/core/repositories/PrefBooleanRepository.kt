package com.dadino.quickstart3.core.repositories

import android.content.SharedPreferences
import com.dadino.quickstart3.core.components.IRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

abstract class PrefBooleanRepository(prefs: SharedPreferences) : PrefRepository(prefs), IBooleanRepository {

	private var subject: BehaviorRelay<Boolean> = BehaviorRelay.create()

	override fun listenOn(): String {
		return key
	}

	override fun onPrefChanged() {
		subject.accept(pref)
	}

	override fun retrieve(): Flowable<Boolean> {
		if (subject.hasValue().not()) {
			subject.accept(pref)
		}
		return subject.toFlowable(BackpressureStrategy.LATEST)
	}

	override fun create(boolean: Boolean): Single<Boolean> {
		return Single.just(editor().putBoolean(key, boolean)
				.commit())
	}

	override fun delete(): Single<Boolean> {
		return Single.just(editor().remove(key)
				.commit())
	}

	override fun update(boolean: Boolean): Single<Boolean> {
		return create(boolean)
	}

	private val pref: Boolean
		get() = pref().getBoolean(key, default)

	protected abstract val default: Boolean
}

interface IBooleanRepository : IRepository {
	fun retrieve(): Flowable<Boolean>
	fun create(boolean: Boolean): Single<Boolean>
	fun delete(): Single<Boolean>
	fun update(boolean: Boolean): Single<Boolean>
}
