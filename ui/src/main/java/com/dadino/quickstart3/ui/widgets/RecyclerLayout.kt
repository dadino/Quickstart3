package com.dadino.quickstart3.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dadino.quickstart3.ui.R
import com.dadino.quickstart3.ui.adapters.BaseAdapter
import com.dadino.quickstart3.ui.utils.Colors
import com.dadino.quickstart3.ui.utils.goneIf

open class RecyclerLayout<T : BaseAdapter<*, *>, E : RecyclerView.LayoutManager> : SwipeRefreshLayout {

	private val emptyTextLabel: TextView by lazy { findViewById<TextView>(R.id.empty_text) }
	protected val list: RecyclerView by lazy { findViewById<RecyclerView>(R.id.list) }

	private var isLoading: Boolean = false
	var adapter: T? = null
		set(adapter) {
			if (isInEditMode) return
			field = adapter
			field?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
				override fun onChanged() {
					super.onChanged()
					updateLoadingState()
					doOnItemsUpdate?.invoke(list, adapter!!)
				}
			})
			list.adapter = field
			updateLoadingState()
		}

	var doOnItemsUpdate: ((RecyclerView, T) -> Unit)? = null

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}

	private fun init() {
		inflate(context, R.layout.view_recycler_layout, this)

		setColorSchemeColors(Colors.getPrimaryColor(context),
				Colors.getAccentColor(context),
				Colors.getPrimaryDarkColor(context))
		clipToPadding = false
		initialize()
	}

	protected fun initialize() {}

	fun setListLoading(loading: Boolean) {
		this.isLoading = loading
		post {
			isRefreshing = isLoading
			updateEmptyTextVisibility()
		}
	}

	fun updateLoadingState() {
		setListLoading(isLoading)
	}

	private fun updateEmptyTextVisibility() {
		val emptyAdapter = adapter?.getItemCount() ?: 0 == 0
		val shouldBeGone = emptyAdapter.not() || isLoading
		emptyTextLabel.goneIf(shouldBeGone)
	}

	fun setEmptyText(text: String) {
		emptyTextLabel.text = text
		updateEmptyTextVisibility()
	}

	fun setEmptyText(@StringRes text: Int) {
		emptyTextLabel.setText(text)
		updateEmptyTextVisibility()
	}
}
