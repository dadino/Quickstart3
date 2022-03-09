package com.dadino.quickstart3.ui.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.dadino.quickstart3.action.Action
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.ContextFormattable
import com.dadino.quickstart3.core.components.EventManager
import com.dadino.quickstart3.core.components.InteractionEventSource
import com.dadino.quickstart3.ui.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable

object ExtendedSnackbar : InteractionEventSource {

	private val eventManager: EventManager = EventManager()

	fun showAsError(
		view: View,
		formattable: ContextFormattable,
		action: Action? = null,
		duration: Int = Snackbar.LENGTH_LONG,
		maxLines: Int = 5,
		onHeightChange: (Int) -> Unit
	) {
		val snackbar = setupSnackbar(view, formattable, duration, action)
		formatAsError(snackbar, maxLines)

		show(snackbar, onHeightChange)
	}

	fun showAsPrimary(
		view: View,
		formattable: ContextFormattable,
		action: Action? = null,
		duration: Int = Snackbar.LENGTH_LONG,
		onHeightChange: (Int) -> Unit
	) {
		val snackbar = setupSnackbar(view, formattable, duration, action)
		formatAsSuccess(snackbar)

		show(snackbar, onHeightChange)
	}

	fun showWithColors(
		view: View,
		formattable: ContextFormattable,
		action: Action? = null,
		duration: Int = Snackbar.LENGTH_LONG,
		@ColorRes backgroundColor: Int,
		@ColorRes textColor: Int,
		onHeightChange: (Int) -> Unit
	) {
		val snackbar = setupSnackbar(view, formattable, duration, action)
		format(snackbar, backgroundColor = backgroundColor, textColor = textColor)
		show(snackbar, onHeightChange)
	}

	private fun setupSnackbar(view: View, formattable: ContextFormattable, duration: Int = Snackbar.LENGTH_LONG, action: Action? = null): Snackbar {
		val snackbar = Snackbar.make(view, formattable.format(view.context) ?: "", duration)
		if (action != null) {
			snackbar.setAction(action.text?.format(view.context)) { eventManager.receiveEvent(action.eventOnClick) }
		}
		return snackbar
	}

	private fun show(snackbar: Snackbar, onHeightChange: (Int) -> Unit) {
		snackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
			override fun onShown(transientBottomBar: Snackbar) {
				onHeightChange(transientBottomBar.view.height)
				super.onShown(transientBottomBar)
			}

			override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
				onHeightChange(0)
				super.onDismissed(transientBottomBar, event)
			}
		})
		snackbar.show()
	}

	private fun formatAsError(snackbar: Snackbar, maxLines: Int = 5) {
		format(snackbar, R.color.colorError, R.color.colorOnError, maxLines = maxLines)
	}

	private fun formatAsSuccess(snackbar: Snackbar) {
		format(snackbar, R.color.primaryColor, R.color.colorOnPrimary)
	}

	private fun format(snackbar: Snackbar, @ColorRes backgroundColor: Int, @ColorRes textColor: Int, maxLines: Int = 5) {
		val snackBarView = snackbar.view

		snackBarView.setBackgroundColor(
			ContextCompat.getColor(snackbar.context, backgroundColor)
		)

		val textView = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
		textView.maxLines = maxLines
		textView.setTextColor(ContextCompat.getColor(snackbar.context, textColor))

		snackbar.setActionTextColor(ContextCompat.getColor(snackbar.context, textColor))
	}

	override fun interactionEvents(): Observable<Event> {
		return eventManager.interactionEvents()
	}
}