package com.dadino.quickstart3.core.utils

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response

fun <E, T : Response<E>> T.toSingle(): Single<E> {
	return if (isSuccessful) Single.just(body()!!) else Single.error<E>(HttpException(this))
}

fun <E, T : Flowable<E>> T.toAsync(): Flowable<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <E, T : Single<E>> T.toAsync(): Single<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <E, T : Observable<E>> T.toAsync(): Observable<E> {
	return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
