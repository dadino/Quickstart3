package com.dadino.quickstart3.sample.viewmodels.grid

import com.dadino.quickstart3.base.Event
import com.dadino.quickstart3.contextformattable.asFormattable
import com.dadino.quickstart3.core.components.BaseViewModel
import com.dadino.quickstart3.core.components.SideEffectHandler
import com.dadino.quickstart3.core.components.Updater
import com.dadino.quickstart3.core.entities.Next
import com.dadino.quickstart3.core.entities.Next.Companion.justState
import com.dadino.quickstart3.core.entities.Next.Companion.noChanges
import com.dadino.quickstart3.core.entities.Start
import com.dadino.quickstart3.core.entities.Start.Companion.start
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.sample.listitems.GridCell
import com.dadino.quickstart3.ui.adapters.ListItem

class GridViewModel : BaseViewModel<GridState>() {

	override fun updater(): Updater<GridState> {
		return GridUpdater()
	}

	override fun getSideEffectHandlers() = listOf<SideEffectHandler>(
	)

}

data class GridState(
	val grid: Map<Int, Int> = mapOf(
		1 to 1,
		2 to 1,
		3 to 1,
		4 to 1,
		5 to 1,
		6 to 1,
		7 to 1,
		8 to 1,
		9 to 1,
		10 to 1,
	)
) : State() {


	fun getListItems(): List<ListItem> {
		val temp = arrayListOf<ListItem>()
		grid.entries.forEach { entry ->
			temp.add(
				GridCell(
					id = "gridCell:${entry.key}",
					message = entry.value.toString().asFormattable(),
					onMinusClick = GridEvent.OnMinusClicked(entry.key),
					onPlusClick = GridEvent.OnPlusClicked(entry.key),
				).apply {
					showInCard = true
					spanSizeRes = when (entry.value) {
						1 -> R.integer.grid_span_size_1
						2 -> R.integer.grid_span_size_2
						3 -> R.integer.grid_span_size_3
						else -> R.integer.grid_span_size_all
					}
				}
			)
		}
		return temp
	}
}

class GridUpdater : Updater<GridState>(true) {

	override fun start(): Start<GridState> {
		return start(getInitialMainState())
	}

	override fun update(previous: GridState, event: Event): Next<GridState> {
		return when (event) {
			is GridEvent.OnMinusClicked -> {
				val map = hashMapOf<Int, Int>()
				map.putAll(previous.grid)
				map[event.index] = ((map[event.index] ?: 0) - 1).coerceAtLeast(1)
				justState(previous.copy(grid = map))
			}
			is GridEvent.OnPlusClicked  -> {
				val map = hashMapOf<Int, Int>()
				map.putAll(previous.grid)
				map[event.index] = ((map[event.index] ?: 0) + 1).coerceAtMost(3)
				justState(previous.copy(grid = map))
			}
			else                        -> noChanges()
		}
	}

	override fun getInitialMainState(): GridState {
		return GridState()
	}

	override fun getInitialSubStates(): List<State> {
		return listOf(
		)
	}
}