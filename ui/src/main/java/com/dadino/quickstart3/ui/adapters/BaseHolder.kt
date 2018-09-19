package com.dadino.quickstart3.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.core.components.InteractionEventSource


abstract class BaseHolder<in T> constructor(itemView: View) :
		RecyclerView.ViewHolder(itemView),
		InteractionEventSource {

	abstract fun bindItem(item: T, position: Int)
}
