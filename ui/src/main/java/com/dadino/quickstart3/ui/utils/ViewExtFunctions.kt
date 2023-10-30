package com.dadino.quickstart3.ui.utils

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.dadino.quickstart3.icon.Icon
import com.google.android.material.button.MaterialButton


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
	visibility = View.VISIBLE
}

fun <T : View> T.invisible() {
	visibility = View.INVISIBLE
}

fun <T : View> T.gone() {
	visibility = View.GONE
}


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
	if (text.toString() != (string ?: "")) setTextKeepState(string ?: "")
}

fun ImageView.setIcon(icon: Icon?, @ColorInt defaultTint: Int? = null) {
	this.goneIf(icon == null)
	if (icon == null) return

	this.setImageResource(icon.icon)

	val tint = icon.tint
	if (tint != null || defaultTint != null) {
		this.imageTintList =
			if (tint != null) ColorStateList.valueOf(ContextCompat.getColor(context, tint))
			else if (defaultTint != null) ColorStateList.valueOf(defaultTint) else null
	}

	val animation = icon.animation
	if (animation != null) this.startAnimation(AnimationUtils.loadAnimation(context, animation)) else clearAnimation()
}

fun MaterialButton.setIcon(icon: Icon?, @ColorInt defaultTint: Int? = null) {
	if (icon == null) {
		this.icon = null
	} else {
		this.setIconResource(icon.icon)

		val tint = icon.tint
		if (tint != null || defaultTint != null) {
			this.iconTint =
				if (tint != null) ColorStateList.valueOf(ContextCompat.getColor(context, tint))
				else if (defaultTint != null) ColorStateList.valueOf(defaultTint) else null
		}
	}
}

