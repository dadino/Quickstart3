package com.dadino.quickstart3.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.core.entities.UserActionable


abstract class BaseHolder<in T> constructor(itemView: View) :
		RecyclerView.ViewHolder(itemView),
		UserActionable {

	abstract fun bindItem(item: T, position: Int)
}
