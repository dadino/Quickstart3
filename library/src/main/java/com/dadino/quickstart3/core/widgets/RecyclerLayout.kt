package com.dadino.quickstart3.core.widgets

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.TextView
import com.dadino.quickstart3.core.R
import com.dadino.quickstart3.core.adapters.BaseAdapter
import com.dadino.quickstart3.core.utils.Colors
import com.dadino.quickstart3.core.utils.goneIf

open class RecyclerLayout<T : BaseAdapter<*, *>, E : RecyclerView.LayoutManager> : SwipeRefreshLayout {

	private val mList: RecyclerView by lazy { findViewById<RecyclerView>(R.id.list) }
	private val mEmptyText: TextView by lazy { findViewById<TextView>(R.id.empty_text) }
	protected var mAdapter: T? = null
	protected lateinit var mLayoutManager: E
	private var mLoading: Boolean = false

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
		this.mLoading = loading
		post {
			isRefreshing = mLoading
			updateEmptyTextVisibility()
		}
	}

	fun updateLoadingState() {
		setListLoading(mLoading)
	}

	private fun updateEmptyTextVisibility() {
		val emptyAdapter = mAdapter == null || mAdapter!!.getItemCount() == 0
		val shouldBeGone = emptyAdapter.not() || mLoading
		mEmptyText.goneIf(shouldBeGone)
	}

	fun setEmptyText(text: String) {
		mEmptyText.text = text
		updateEmptyTextVisibility()
	}

	fun setEmptyText(@StringRes text: Int) {
		mEmptyText.setText(text)
		updateEmptyTextVisibility()
	}

	var adapter: T?
		get() = mAdapter
		set(adapter) {
			if (isInEditMode) return
			this.mAdapter = adapter
			mAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
				override fun onChanged() {
					super.onChanged()
					updateLoadingState()
				}
			})
			mList.adapter = mAdapter
			updateLoadingState()
		}

	fun setLayoutManager(layoutManager: E) {
		this.mLayoutManager = layoutManager
		mList.layoutManager = layoutManager
	}

	fun setBottomPadding(paddingInPixel: Int) {
		mList.setPadding(paddingLeft, paddingTop, paddingRight, paddingInPixel)
	}

	fun addItemDecoration(itemDecorator: RecyclerView.ItemDecoration) {
		mList.addItemDecoration(itemDecorator)
	}

	fun setHasFixedSize(hasFixedSize: Boolean) {
		mList.setHasFixedSize(hasFixedSize)
	}
}
