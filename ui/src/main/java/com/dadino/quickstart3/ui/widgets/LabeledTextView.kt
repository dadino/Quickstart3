package com.dadino.quickstart3.ui.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.dadino.quickstart3.ui.R
import com.dadino.quickstart3.ui.utils.goneIf

class LabeledTextView @kotlin.jvm.JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
	private val label: TextView by lazy { findViewById<TextView>(R.id.labeled_textview_label) }
	private val value: TextView by lazy { findViewById<TextView>(R.id.labeled_textview_value) }

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
		label.setText(labelRes)
	}

	fun setLabel(label: String?) {
		this.label.text = label
	}

	fun setValue(value: String?) {
		goneIf(TextUtils.isEmpty(value))
		this.value.text = value
	}
}