package com.dadino.quickstart3.ui.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.dadino.quickstart3.core.interfaces.ErrorHandler
import com.dadino.quickstart3.ui.R
import com.google.android.material.snackbar.Snackbar

object ErrorSnackbar {

	fun showErrorSnackbar(view: View, throwable: Throwable?, errorHandler: ErrorHandler, duration: Int = Snackbar.LENGTH_LONG, @StringRes actionLabel: Int = 0, action: (() -> Unit)? = null) {
		val errorMessageForUser = errorHandler.formatError(throwable)
		val snackbar = Snackbar.make(view, errorMessageForUser, duration)
		if (action != null) {
			snackbar.setAction(actionLabel) { action() }
		}
		formatAsError(snackbar)
		snackbar.show()
	}

	fun showSuccessSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG, @StringRes actionLabel: Int = 0, action: (() -> Unit)? = null) {
		val snackbar = Snackbar.make(view, message, duration)
		if (action != null) {
			snackbar.setAction(actionLabel) { action() }
		}
		formatAsSuccess(snackbar)
		snackbar.show()
	}

	private fun formatAsError(snackbar: Snackbar) {
		format(snackbar, R.color.colorError, R.color.textColorOnError)
	}

	private fun formatAsSuccess(snackbar: Snackbar) {
		format(snackbar, R.color.colorSuccess, R.color.textColorOnSuccess)
	}

	private fun format(snackbar: Snackbar, @ColorRes backgroundColor: Int,
					   @ColorRes textColor: Int) {
		val snackBarView = snackbar.view

		snackBarView.setBackgroundColor(
				ContextCompat.getColor(snackbar.context, backgroundColor))

		val textView = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
		textView.maxLines = 5
		textView.setTextColor(ContextCompat.getColor(snackbar.context, textColor))

		snackbar.setActionTextColor(ContextCompat.getColor(snackbar.context, textColor))
	}
}