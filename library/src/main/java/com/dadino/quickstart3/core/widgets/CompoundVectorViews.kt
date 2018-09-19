package com.dadino.quickstart3.core.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.TextView
import com.dadino.quickstart3.core.R

class CompoundVectorTextView : AppCompatTextView {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		initAttrs(attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		initAttrs(attrs)
	}

	internal fun initAttrs(attrs: AttributeSet?) {
		CompoundVectorInflater.inflate(this, attrs)
	}
}

class CompoundVectorEditText : AppCompatEditText {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		initAttrs(attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		initAttrs(attrs)
	}

	internal fun initAttrs(attrs: AttributeSet?) {
		CompoundVectorInflater.inflate(this, attrs)
	}
}

class CompoundVectorButton : AppCompatButton {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		initAttrs(attrs)
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		initAttrs(attrs)
	}

	internal fun initAttrs(attrs: AttributeSet?) {
		CompoundVectorInflater.inflate(this, attrs)
	}
}

object CompoundVectorInflater {
	internal fun inflate(view: TextView, attrs: AttributeSet?) {
		if (attrs != null) {
			val attributeArray = view.context.obtainStyledAttributes(
					attrs,
					R.styleable.CompountVector)

			var drawableLeft: Drawable? = null
			var drawableRight: Drawable? = null
			var drawableBottom: Drawable? = null
			var drawableTop: Drawable? = null
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				drawableLeft = attributeArray.getDrawable(R.styleable.CompountVector_drawableLeftCompat)
				drawableRight = attributeArray.getDrawable(R.styleable.CompountVector_drawableRightCompat)
				drawableBottom = attributeArray.getDrawable(R.styleable.CompountVector_drawableBottomCompat)
				drawableTop = attributeArray.getDrawable(R.styleable.CompountVector_drawableTopCompat)
			} else {
				val drawableLeftId = attributeArray.getResourceId(R.styleable.CompountVector_drawableLeftCompat, -1)
				val drawableRightId = attributeArray.getResourceId(R.styleable.CompountVector_drawableRightCompat, -1)
				val drawableBottomId = attributeArray.getResourceId(R.styleable.CompountVector_drawableBottomCompat, -1)
				val drawableTopId = attributeArray.getResourceId(R.styleable.CompountVector_drawableTopCompat, -1)

				if (drawableLeftId != -1)
					drawableLeft = AppCompatResources.getDrawable(view.context, drawableLeftId)
				if (drawableRightId != -1)
					drawableRight = AppCompatResources.getDrawable(view.context, drawableRightId)
				if (drawableBottomId != -1)
					drawableBottom = AppCompatResources.getDrawable(view.context, drawableBottomId)
				if (drawableTopId != -1)
					drawableTop = AppCompatResources.getDrawable(view.context, drawableTopId)
			}
			view.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
			attributeArray.recycle()
		}
	}
}