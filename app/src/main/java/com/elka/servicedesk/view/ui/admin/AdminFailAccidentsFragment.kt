package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AdminAccidentsFragmentBinding
import com.elka.servicedesk.databinding.EngineerAccidentsInWorkFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.allToAccidentItems
import com.elka.servicedesk.view.list.accidents.AccidentItemViewHolder
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter

class AdminFailAccidentsFragment : AdminBaseFragment() {
    private lateinit var binding: AdminAccidentsFragmentBinding

    private val accidentsAdapterListener by lazy {
        object : AccidentItemViewHolder.Companion.Listener {
            override fun onSelect(accident: Accident) {
                hideMenu()
                goAccident(accident.id, FROM_FAIL_ACCIDENTS_TO_ACCIDENT)
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
        val items = list.allToAccidentItems()
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
        binding = AdminAccidentsFragmentBinding.inflate(layoutInflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            master = this@AdminFailAccidentsFragment
            viewModel = this@AdminFailAccidentsFragment.accidentViewModel
            accidentsAdapter = this@AdminFailAccidentsFragment.accidentsAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        accidentViewModel.loadFailedAccidents()
    }

    override fun onResume() {
        super.onResume()

        showMenu()
        if (accidentViewModel.failedAccidents.value!!.isEmpty()) reloadAccidents()

        userViewModel.work.observe(this, workObserver)
        userViewModel.error.observe(this, errorObserver)
        userViewModel.externalAction.observe(this, externalActionObserver)

        accidentViewModel.work.observe(this, workObserver)
        accidentViewModel.error.observe(this, errorObserver)
        accidentViewModel.failedFilteredAccidents.observe(this, accidentsObserver)
    }

    override fun onStop() {
        super.onStop()

        userViewModel.work.removeObserver(workObserver)
        userViewModel.error.removeObserver(errorObserver)
        userViewModel.externalAction.removeObserver(externalActionObserver)

        accidentViewModel.work.removeObserver(workObserver)
        accidentViewModel.error.removeObserver(errorObserver)
        accidentViewModel.failedFilteredAccidents.removeObserver(accidentsObserver)
    }
}