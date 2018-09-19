package com.dadino.quickstart3.core.utils

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.view.ViewTreeObserver
import android.widget.EditText
import com.dadino.quickstart3.core.entities.Optional
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response

inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
	viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
		override fun onGlobalLayout() {
			if (measuredWidth > 0 && measuredHeight > 0) {
				viewTreeObserver.removeOnGlobalLayoutListener(this)
				f()
			}
		}
	})

}

inline fun <T : EditText> T.onTextChanged(crossinline f: T.() -> Unit): TextWatcher {
	val textWatcher: TextWatcher = object : TextWatcher {
		override fun afterTextChanged(s: Editable?) {
			f()
		}

		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		}

	}
	addTextChangedListener(textWatcher)
	return textWatcher
}

fun <T : EditText> T.setTextWithoutTriggering(string: String, textWatcher: TextWatcher) {
	removeTextChangedListener(textWatcher)
	if (text.toString() != string) {
		setText(string)
		setSelection(text.length)
	}
	addTextChangedListener(textWatcher)
}

fun <T : EditText> T.setTextIfNew(string: String?) {
	if (text.toString() != string ?: "") setTextKeepState(string ?: "")
}

fun String.isValidEmail(): Boolean {
	return this.isEmpty().not() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(minimumLength: Int): Boolean {
	return this.length >= minimumLength
}

fun <T : View> T.visibleIf(visible: Boolean) {
	if (visible) this.visible() else this.gone()
}

fun <T : View> T.invisibleIf(invisible: Boolean) {
	if (invisible) this.invisible() else this.visible()
}

fun <T : View> T.goneIf(gone: Boolean) {
	if (gone) this.gone() else this.visible()
}

fun <T : View> T.visible() {
	visibility = VISIBLE
}

fun <T : View> T.invisible() {
	visibility = INVISIBLE
}

fun <T : View> T.gone() {
	visibility = GONE
}

fun <T : Collection<*>> T?.isNullOrEmpty(): Boolean {
	return this == null || this.isEmpty()
}

fun <E, T : Collection<E>> T.firstOptional(): Optional<E> {
	return Optional.create(firstOrNull())
}

inline fun <E, T : List<E>> T.toggleElement(element: E, crossinline compare: T.(E) -> Boolean): List<E> {
	val elementInList = firstOrNull { compare(it) }
	val newCompList = arrayListOf<E>()
	newCompList.addAll(this)
	if (elementInList == null) {
		newCompList.add(element)
	} else {
		newCompList.remove(elementInList)
	}
	return newCompList.toList()
}

inline fun <E, T : List<E>> T.modifyElement(crossinline compare: T.(E) -> Boolean, crossinline change: T.(E) -> E): List<E> {
	val elementInList = firstOrNull { compare(it) }

	if (elementInList != null) {
		change(elementInList)
	}
	return this
}

fun String.isNullOrEmpty(): Boolean {
	return TextUtils.isEmpty(this)
}

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
