package com.dadino.quickstart3.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart3.core.entities.State
import com.dadino.quickstart3.core.entities.VMStarter
import com.dadino.quickstart3.core.fragments.BaseFragment
import com.dadino.quickstart3.core.utils.QuickLogger
import com.dadino.quickstart3.sample.R
import com.dadino.quickstart3.sample.viewmodels.grid.GridState
import com.dadino.quickstart3.sample.viewmodels.grid.GridViewModel
import com.dadino.quickstart3.ui.adapters.GenericAdapter
import com.dadino.quickstart3.ui.adapters.grid.setupAsGrid
import org.koin.android.viewmodel.ext.android.viewModel

class GridFragment : BaseFragment() {

  private lateinit var grid: RecyclerView

  private val gridViewModel: GridViewModel by viewModel()

  override fun viewModels(): List<VMStarter> {
	return listOf(
	  VMStarter { gridViewModel }
	)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
	return inflater.inflate(R.layout.fragment_grid, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
	super.onViewCreated(view, savedInstanceState)
	grid = view.findViewById<RecyclerView>(R.id.recycler_view)

	grid.setupAsGrid(
	  spanCountRes = R.integer.grid_span_size_all,
	  verticalSpacing = R.dimen._8dp,
	  horizontalSpacing = R.dimen._8dp,
	)

	eventManager.attachEventSources(
	  mapOf(
		"list" to (grid.adapter as GenericAdapter).interactionEvents()
	  )
	)
  }

  override fun renderState(state: State) {
	when (state) {
	  is GridState -> {
		render(state)
	  }

	}
  }

  private fun render(state: GridState) {
	QuickLogger.tag("Grid").d { "State: $state" }
	(grid.adapter as GenericAdapter).setItemsAsync(state.getListItems())
  }
}