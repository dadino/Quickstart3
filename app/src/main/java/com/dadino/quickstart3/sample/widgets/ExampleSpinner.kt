package com.dadino.quickstart3.sample.widgets


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dadino.quickstart3.core.components.InteractionEventSource
import com.dadino.quickstart3.core.entities.Event
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.sample.entities.ExampleData
import com.dadino.quickstart3.sample.viewmodels.SpinnerEvent
import com.dadino.quickstart3.ui.adapters.BaseHolder
import com.dadino.quickstart3.ui.adapters.BaseSpinnerAdapter
import com.dadino.quickstart3.ui.widgets.LoadingSpinner
import com.dadino.quickstart3.ui.widgets.OnItemSelected
import io.reactivex.Observable


class ExampleSpinner : LoadingSpinner<ExampleData, ExampleDataSpinnerAdapter>, InteractionEventSource {

	override fun interactionEvents(): Observable<Event> {
		return super.interactionEvents().map {
			when (it) {
				is OnItemSelected -> SpinnerEvent.OnExampleDataSelected(selectedItem)
				else              -> it
			}
		}
	}

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	init {
		adapter = ExampleDataSpinnerAdapter()
		setLabel(R.string.login_example_data_selector)
	}
}


class ExampleDataSpinnerAdapter : BaseSpinnerAdapter<ExampleData, ExampleDataHolder>() {

	override fun getItemId(position: Int): Long {
		return getItem(position).id
	}

	override fun modifyDropDownView(view: View): View {
		return view.apply {
			val padding = view.resources.getDimensionPixelSize(R.dimen._16dp)
			setPadding(padding, padding, padding, padding)
		}
	}

	override fun createHolder(convertView: View): ExampleDataHolder {
		return ExampleDataHolder(convertView)
	}

	override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
		return inflater.inflate(ExampleDataHolder.layoutId, parent, false)
	}

}

class ExampleDataHolder(view: View) : BaseHolder<ExampleData>(view) {
	override fun interactionEvents(): Observable<Event> {
		return Observable.empty()
	}

	private val exampleDataName: TextView by lazy { view.findViewById<TextView>(R.id.example_data_name) }

	private lateinit var order: ExampleData

	override fun bindItem(item: ExampleData, position: Int) {
		order = item
		exampleDataName.text = item.displayName
	}

	companion object {
		const val layoutId: Int = R.layout.item_example_data
	}
}