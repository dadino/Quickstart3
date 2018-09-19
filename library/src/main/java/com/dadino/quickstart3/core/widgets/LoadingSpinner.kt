package com.dadino.quickstart3.core.widgets

import android.content.Context
import android.support.annotation.StringRes
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import com.dadino.quickstart3.core.R
import com.dadino.quickstart3.core.adapters.BaseSpinnerAdapter
import com.dadino.quickstart3.core.entities.DoNotReactToThisAction
import com.dadino.quickstart3.core.entities.UserAction
import com.dadino.quickstart3.core.entities.UserActionable
import com.dadino.quickstart3.core.utils.gone
import com.dadino.quickstart3.core.utils.visible
import com.jakewharton.rxbinding2.widget.RxAdapterView
import io.reactivex.Observable

abstract class LoadingSpinner<ITEM, T : BaseSpinnerAdapter<ITEM, *>> : FrameLayout, UserActionable {

	var adapter: T? = null

	protected val progress: ProgressBar by lazy { findViewById<ProgressBar>(R.id.loading_spinner_progress) }
	protected val spinner: Spinner by lazy { findViewById<Spinner>(R.id.loading_spinner_spinner) }
	protected val label: TextView by lazy { findViewById<TextView>(R.id.loading_spinner_label) }
	protected val retryAction: Button by lazy { findViewById<Button>(R.id.loading_spinner_action_retry) }

	private var labelString: String? = null
	private var retryString: String? = null

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		setLabelFromAttributeSet(context, attrs)
		init()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		setLabelFromAttributeSet(context, attrs)
		init()
	}

	private fun init() {
		View.inflate(context, R.layout.view_loading_spinner, this)

		setLabel(labelString)
		retryAction.text = retryString
		setOnClickListener { spinner.performClick() }
		initialize()
	}

	private fun setLabelFromAttributeSet(context: Context, attrs: AttributeSet?) {
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingSpinner)
			labelString = a.getString(R.styleable.LoadingSpinner_ls_label)
			retryString = a.getString(R.styleable.LoadingSpinner_ls_retry)
			a.recycle()
		}
	}

	protected fun initialize() {}

	fun setState(items: List<ITEM>, loading: Boolean, inError: Boolean) {
		if (loading.not() && inError) {
			progress.gone()
			spinner.gone()
			label.gone()
			retryAction.visible()
		} else if (loading && inError.not()) {
			progress.visible()
			spinner.gone()
			label.visible()
			retryAction.gone()
		} else if (loading.not() && inError.not()) {
			progress.gone()
			spinner.visible()
			label.visible()
			retryAction.gone()
		}

		val sel = selectedId
		adapter?.items = items
		spinner.adapter = adapter
		selectedId = sel
	}

	fun setLabel(@StringRes stringId: Int) {
		label.text = context.getString(stringId)
	}

	fun setLabel(string: String?) {
		label.text = string
		if (TextUtils.isEmpty(string))
			label.visibility = View.GONE
		else
			label.visibility = View.VISIBLE
	}

	fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener) {
		spinner.onItemSelectedListener = listener
	}

	fun setOnRetryClickListener(listener: OnClickListener) {
		retryAction.setOnClickListener(listener)
	}

	var selectedItem: ITEM? = null
		get() {
			return adapter?.findItem(selection)
		}

	var transientSelectedPosition: Int = -1
	var selection: Int
		get() = spinner.selectedItemPosition
		set(position) {
			if (position == -1) return
			if (position == spinner.selectedItemPosition) return
			spinner.setSelection(position)
		}

	var selectedId: Long
		get() {
			return if (spinner.selectedItemPosition >= 0)
				adapter?.getItemId(spinner.selectedItemPosition) ?: 0 else -1
		}
		set(id) {
			val wantedPosition = adapter?.getPosition(id) ?: 0
			if (wantedPosition < 0 || wantedPosition == spinner.selectedItemPosition) return
			spinner.setSelection(wantedPosition)
		}

	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		spinner.isEnabled = enabled
		label.isEnabled = enabled
		retryAction.isEnabled = enabled
	}


	override fun userActions(): Observable<UserAction> {
		return RxAdapterView.itemSelections(spinner).map { position ->
			when (position) {
				transientSelectedPosition -> {
					Log.d("Spinner", "New position is $position, old position is $transientSelectedPosition, DO NOT REACT")
					DoNotReactToThisAction()
				}
				else                      -> {
					Log.d("Spinner", "New position is $position, old position is $transientSelectedPosition, REACT")
					transientSelectedPosition = position
					OnItemSelected()
				}
			}
		}
	}
}

class OnItemSelected : UserAction()
