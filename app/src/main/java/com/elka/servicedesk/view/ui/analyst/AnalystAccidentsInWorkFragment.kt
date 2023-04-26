package com.elka.servicedesk.view.ui.analyst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AnalystAccidentsFragmentBinding
import com.elka.servicedesk.databinding.AnalystAccidentsInWorkFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.view.list.accidents.AccidentViewHolder
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter

class AnalystAccidentsInWorkFragment : AnalystBaseFragment() {
  private lateinit var binding: AnalystAccidentsInWorkFragmentBinding

  private val accidentsAdapterListener by lazy {
    object : AccidentViewHolder.Companion.Listener {
      override fun onSelect(accident: Accident) {
        Toast.makeText(requireContext(), accident.message, Toast.LENGTH_SHORT).show()
      }
    }
  }

  private val accidentsAdapter by lazy {
    AccidentsAdapter(accidentsAdapterListener)
  }

  private val accidentsObserver = Observer<List<Accident>> {
    accidentsAdapter.setItems(it)
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
    binding = AnalystAccidentsInWorkFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AnalystAccidentsInWorkFragment
      viewModel = this@AnalystAccidentsInWorkFragment.accidentViewModel
      accidentsAdapter = this@AnalystAccidentsInWorkFragment.accidentsAdapter
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
    accidentViewModel.loadAnalystAccidents(userViewModel.profile.value!!.id)
  }

  override fun onResume() {
    super.onResume()

    if (accidentViewModel.analystAccidents.value!!.isEmpty()) reloadAccidents()

    userViewModel.work.observe(this, workObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.externalAction.observe(this, externalActionObserver)

    accidentViewModel.work.observe(this, workObserver)
    accidentViewModel.error.observe(this, errorObserver)
    accidentViewModel.analystsFilteredAccidents.observe(this, accidentsObserver)
  }

  override fun onStop() {
    super.onStop()

    userViewModel.work.removeObserver(workObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)

    accidentViewModel.work.removeObserver(workObserver)
    accidentViewModel.error.removeObserver(errorObserver)
    accidentViewModel.analystsFilteredAccidents.removeObserver(accidentsObserver)
  }
}