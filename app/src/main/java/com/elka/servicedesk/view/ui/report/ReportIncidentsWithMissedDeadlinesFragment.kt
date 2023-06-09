package com.elka.servicedesk.view.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ReportIncidentsWithMissedDeadlinesFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.accidentsByDivisions
import com.elka.servicedesk.view.list.accidents.AccidentItemViewHolder
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter

class ReportIncidentsWithMissedDeadlinesFragment : ReportBaseFragment() {
	private lateinit var binding: ReportIncidentsWithMissedDeadlinesFragmentBinding

	private val accidentsAdapterListener by lazy {
		object : AccidentItemViewHolder.Companion.Listener {
			override fun onSelect(accident: Accident) {
				hideMenu()
				goAccident(accident.id, FROM_REPORT_INCIDENTS_WITH_MISSED_DEADLINE_TO_ACCIDENT)
			}
		}
	}

	private val accidentsAdapter by lazy {
		AccidentsAdapter(accidentsAdapterListener)
	}

	override fun onTickTimer() {
		super.onTickTimer()
		accidentsAdapter.updateItems()
	}

	private val accidentsObserver = Observer<List<Accident>> { list ->
		val items = list.accidentsByDivisions()
		accidentsAdapter.setItems(items)
	}
	override val workObserver = Observer<List<Work>> {
		binding.swiper.isRefreshing = hasLoads
	}

	override val externalActionObserver = Observer<Action?> {
		if (it == null) return@Observer
		super.externalActionObserver.onChanged(it)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding =
			ReportIncidentsWithMissedDeadlinesFragmentBinding.inflate(layoutInflater, container, false)
		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			master = this@ReportIncidentsWithMissedDeadlinesFragment
			viewModel = this@ReportIncidentsWithMissedDeadlinesFragment.reportViewModel
			accidentsAdapter = this@ReportIncidentsWithMissedDeadlinesFragment.accidentsAdapter
		}

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

//		activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)

		val refresherColor = requireContext().getColor(R.color.accent)
		val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener { reloadAccidents() }

		binding.swiper.setColorSchemeColors(refresherColor)
		binding.swiper.setOnRefreshListener(swipeRefreshListener)

		val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
		binding.accidentsList.addItemDecoration(decorator)

		binding.layoutNoFound.message.text = getString(R.string.list_empty)
	}


	private fun reloadAccidents() {
		if (hasLoads) return
		reportViewModel.loadIncidentsWithMissedDeadline()
	}

	override fun onResume() {
		super.onResume()

		showMenu()
		if (reportViewModel.incidentsWithMissedDeadline.value!!.isEmpty()) reloadAccidents()

		userViewModel.work.observe(this, workObserver)
		userViewModel.error.observe(this, errorObserver)
		userViewModel.externalAction.observe(this, externalActionObserver)

		reportViewModel.work.observe(this, workObserver)
		reportViewModel.error.observe(this, errorObserver)
		reportViewModel.incidentsWithMissedDeadlineFiltered.observe(this, accidentsObserver)
	}

	override fun onStop() {
		super.onStop()

		userViewModel.work.removeObserver(workObserver)
		userViewModel.error.removeObserver(errorObserver)
		userViewModel.externalAction.removeObserver(externalActionObserver)

		reportViewModel.work.removeObserver(workObserver)
		reportViewModel.error.removeObserver(errorObserver)
		reportViewModel.incidentsWithMissedDeadlineFiltered.removeObserver(accidentsObserver)
	}
}
