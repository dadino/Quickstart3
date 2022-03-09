package com.dadino.quickstart3.core.repositories

import android.content.SharedPreferences
import com.dadino.quickstart3.base.Optional
import com.dadino.quickstart3.core.components.IRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

abstract class PrefStringRepository(prefs: SharedPreferences) : PrefRepository(prefs), IStringRepository {

	private var subject: BehaviorRelay<Optional<String>> = BehaviorRelay.create()

	override protected fun listenOn(): String {
		return key
	}

	override protected fun onPrefChanged() {
		subject.accept(Optional.create(pref))
	}

	override fun retrieve(): Flowable<Optional<String>> {
		if (subject.hasValue().not()) {
			subject.accept(Optional.create(pref))
		}
		return subject.toFlowable(BackpressureStrategy.LATEST)
	}

	override fun create(string: String): Single<Boolean> {
		return Single.just(editor().putString(key, string)
				.commit())
	}

	override fun delete(): Single<Boolean> {
		return Single.just(editor().remove(key)
				.commit())
	}

	override fun update(string: String): Single<Boolean> {
		return create(string)
	}

	private val pref: String?
		get() = pref().getString(key, default)

	protected abstract val default: String?
}

interface IStringRepository : IRepository {

	fun retrieve(): Flowable<Optional<String>>
	fun create(string: String): Single<Boolean>
	fun delete(): Single<Boolean>
	fun update(string: String): Single<Boolean>
}