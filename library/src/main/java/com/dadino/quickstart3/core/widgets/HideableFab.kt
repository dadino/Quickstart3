package com.dadino.quickstart3.core.widgets

import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.AttributeSet

open class HideableFab : FloatingActionButton {

	var canShow = true
		set(value) {
			field = value
			if (canShow.not()) {
				hide()
			} else {
				show()
			}
		}

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun show() {
		if (canShow) super.show()
	}
}
