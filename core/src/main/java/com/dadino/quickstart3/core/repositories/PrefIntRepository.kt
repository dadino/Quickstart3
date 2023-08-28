package com.dadino.quickstart3.core.repositories

import android.content.SharedPreferences
import com.dadino.quickstart3.core.components.IRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

abstract class PrefIntRepository(prefs: SharedPreferences) : PrefRepository(prefs), IIntRepository {

	private var subject: BehaviorRelay<Int> = BehaviorRelay.create()

	override fun listenOn(): String {
		return key
	}

	override fun onPrefChanged() {
		subject.accept(pref)
	}

	override fun retrieve(): Flowable<Int> {
		if (subject.hasValue().not()) {
			subject.accept(pref)
		}
		return subject.toFlowable(BackpressureStrategy.LATEST)
	}

	override fun create(int: Int): Single<Boolean> {
		return Single.just(editor().putInt(key, int)
				.commit())
	}

	override fun delete(): Single<Boolean> {
		return Single.just(editor().remove(key)
				.commit())
	}

	override fun update(int: Int): Single<Boolean> {
		return create(int)
	}

	private val pref: Int
		get() = pref().getInt(key, default)

	protected abstract val default: Int
}

interface IIntRepository : IRepository {
	fun retrieve(): Flowable<Int>
	fun create(int: Int): Single<Boolean>
	fun delete(): Single<Boolean>
	fun update(int: Int): Single<Boolean>
}

