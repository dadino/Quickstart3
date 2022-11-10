package com.dadino.quickstart3.ui.adapters.grid

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class GridDividerItemDecoration : RecyclerView.ItemDecoration() {

	var spanCount: Int = 1
	var verticalSpacingInPx: Int = 0
	var horizontalSpacingInPx: Int = 0

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		super.getItemOffsets(outRect, view, parent, state)

		val params = view.layoutParams as GridLayoutManager.LayoutParams

		val spanIndex = params.spanIndex
		val spanSize = params.spanSize

		// If it is in column 0 you apply the full offset on the start side, else only half
		if (spanIndex == 0) {
			outRect.left = horizontalSpacingInPx
		} else {
			outRect.left = horizontalSpacingInPx / 2
		}

		// If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
		if (spanIndex + spanSize == spanCount) {
			outRect.right = horizontalSpacingInPx
		} else {
			outRect.right = horizontalSpacingInPx / 2
		}

		// just add some vertical padding as well
		outRect.top = verticalSpacingInPx / 2
		outRect.bottom = verticalSpacingInPx / 2

		if (isLayoutRTL(parent)) {
			val tmp = outRect.left
			outRect.left = outRect.right
			outRect.right = tmp
		}
	}

	@SuppressLint("NewApi", "WrongConstant")
	private fun isLayoutRTL(parent: RecyclerView): Boolean {
		return parent.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
	}
}
