package com.dadino.quickstart3.core.utils

import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.Response

fun <E, T : Response<E>> T.toSingle(): Single<E> {
	return if (isSuccessful) Single.just(body()!!) else Single.error<E>(HttpException(this))
}