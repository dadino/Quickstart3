package com.dadino.quickstart3.ui.adapters.grid

import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.ui.adapters.GenericAdapter


fun RecyclerView.setupAsGrid(
	@IntegerRes spanCountRes: Int,
	@DimenRes verticalSpacing: Int,
	@DimenRes horizontalSpacing: Int
) {
	val genericAdapter = GenericAdapter()
	this.adapter = genericAdapter
	val gridSpanCount = resources.getInteger(spanCountRes)
	val gridLayoutManager = GridLayoutManager(this.context, gridSpanCount)

	val spanSizeLookup = ListItemSpanSizeLookup(genericAdapter, gridLayoutManager, resources, spanCountRes)
	spanSizeLookup.isSpanIndexCacheEnabled = true
	spanSizeLookup.isSpanGroupIndexCacheEnabled = true
	gridLayoutManager.spanSizeLookup = spanSizeLookup

	this.layoutManager = gridLayoutManager

	val gridDivider = GridDividerItemDecoration()
	gridDivider.spanCount = gridSpanCount
	gridDivider.verticalSpacingInPx = resources.getDimensionPixelSize(verticalSpacing)
	gridDivider.horizontalSpacingInPx = resources.getDimensionPixelSize(horizontalSpacing)
	this.addItemDecoration(gridDivider)
}

fun RecyclerView.changeGridSpanCount(spanCount: Int) {
	val layoutManager = layoutManager
	if (layoutManager is GridLayoutManager && layoutManager.spanCount != spanCount) {
		for (i in 0 until this.itemDecorationCount) {
			val itemDecorator = this.getItemDecorationAt(i)
			if (itemDecorator is GridDividerItemDecoration) {
				itemDecorator.spanCount = spanCount
			}
		}
		layoutManager.spanCount = spanCount
	}
}