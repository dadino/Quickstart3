package com.dadino.quickstart3.core.utils

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <E, T : Flowable<E>> T.toAsync(): Flowable<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <E, T : Single<E>> T.toAsync(): Single<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <E, T : Observable<E>> T.toAsync(): Observable<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : Completable> T.toAsync(): Completable {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
