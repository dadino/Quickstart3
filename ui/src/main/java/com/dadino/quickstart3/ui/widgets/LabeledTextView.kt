package com.dadino.quickstart3.ui.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import com.dadino.quickstart3.ui.R
import com.dadino.quickstart3.ui.utils.goneIf
import kotlinx.android.synthetic.main.view_labeled_textview.view.*

class LabeledTextView @kotlin.jvm.JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null,
		defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
	init {
		orientation = VERTICAL
		View.inflate(context, R.layout.view_labeled_textview, this)
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.LabeledTextView)
			setLabel(a.getString(R.styleable.LabeledTextView_lt_label))
			setValue(a.getString(R.styleable.LabeledTextView_lt_value))
			a.recycle()
		}
	}


	fun setLabel(@StringRes labelRes: Int) {
		labeled_textview_label.setText(labelRes)
	}

	fun setLabel(label: String?) {
		labeled_textview_label.text = label
	}

	fun setValue(value: String?) {
		goneIf(TextUtils.isEmpty(value))
		labeled_textview_value.text = value
	}
}