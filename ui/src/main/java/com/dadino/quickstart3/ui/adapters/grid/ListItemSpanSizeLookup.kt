package com.dadino.quickstart3.ui.adapters.grid

import android.content.res.Resources
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.GridLayoutManager
import com.dadino.quickstart3.ui.adapters.GenericAdapter

class ListItemSpanSizeLookup(
	private val genericAdapter: GenericAdapter,
	private val gridLayoutManager: GridLayoutManager,
	private val resources: Resources,
	@IntegerRes private val spanCountRes: Int
) : GridLayoutManager.SpanSizeLookup() {

	override fun getSpanSize(position: Int): Int {
		return resources.getInteger(
			genericAdapter.getItem(position)?.spanSizeRes ?: spanCountRes
		).coerceAtMost(gridLayoutManager.spanCount)
	}
}