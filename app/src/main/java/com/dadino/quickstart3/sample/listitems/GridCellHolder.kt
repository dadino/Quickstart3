package com.dadino.quickstart3.sample.listitems

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.base.NoOpEvent
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.ui.adapters.ListItem
import com.dadino.quickstart3.ui.adapters.ListItemHolder
import com.dadino.quickstart3.ui.adapters.UpdatableHolder
import com.dadino.quickstart3.ui.utils.visibleIf
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable

class GridCellHolder(view: View) : ListItemHolder(view), UpdatableHolder {

	private val root: View by lazy { view.findViewById<View>(R.id.item_root) }
	private val text: TextView by lazy { view.findViewById<TextView>(R.id.gridcell_message) }
	private val minus: ImageView by lazy { view.findViewById<ImageView>(R.id.gridcell_minus) }
	private val plus: ImageView by lazy { view.findViewById<ImageView>(R.id.gridcell_plus) }

	private var gridCell: GridCell? = null

	override fun bindItem(item: ListItem, position: Int) {
		when (item) {
			is GridCell -> {
				gridCell = item

				setMessage(item)
			}
		}
	}

	private fun setMessage(item: GridCell) {
		text.visibleIf(item.message != null)
		text.text = item.message?.format(text.context)
	}

	override fun updateWithPayloads(item: ListItem, payloads: MutableList<Any>) {
		when (item) {
			is GridCell -> {
				val bundle = payloads.first() as Bundle

				gridCell = item
				if (bundle.containsKey(PAYLOAD_MESSAGE)) setMessage(item)
			}
		}
	}

	override fun interactionEvents(): Observable<Event> {
		return Observable.merge(
			listOf(
				minus.clicks().map { gridCell?.onMinusClick ?: NoOpEvent },
				plus.clicks().map { gridCell?.onPlusClick ?: NoOpEvent },
			)
		)
	}

	companion object {

		const val layoutId = R.layout.item_grid_cell

		const val PAYLOAD_MESSAGE = "PAYLOAD_MESSAGE"
	}
}