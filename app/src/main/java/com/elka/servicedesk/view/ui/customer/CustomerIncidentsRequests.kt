package com.elka.servicedesk.view.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.CustomerIncidentsRequestsFragmentBinding
import com.elka.servicedesk.other.AccidentType
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.toAccidentItems
import com.elka.servicedesk.view.dialog.AccidentBottomSheetDialog
import com.elka.servicedesk.view.dialog.AddAccidentDialog
import com.elka.servicedesk.view.list.accidents.AccidentItem
import com.elka.servicedesk.view.list.accidents.AccidentItemViewHolder
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter
import com.elka.servicedesk.view.ui.UserBaseFragment

class CustomerIncidentsRequests : CustomerBaseFragment() {
  private lateinit var binding: CustomerIncidentsRequestsFragmentBinding

  private val accidentsAdapterListener by lazy {
    object : AccidentItemViewHolder.Companion.Listener {
      override fun onSelect(accident: Accident) {
        hideMenu()
        goAccident(accident.id, FROM_CUSTOMER_ACCIDENTS_TO_ACCIDENT)
      }
    }
  }

  private val accidentsAdapter by lazy {
    AccidentsAdapter(accidentsAdapterListener)
  }

  private val accidentsObserver = Observer<List<Accident>> { list ->
    val items = mutableListOf<AccidentItem>()
    val incidents =
      list.filter { it.type == AccidentType.INCIDENT }.sortedByDescending { it.createdDate }
    val requests =
      list.filter { it.type == AccidentType.REQUEST }.sortedByDescending { it.createdDate }

    if (incidents.isNotEmpty()) {
      items.add(
        AccidentItem(
          type = AccidentsAdapter.TYPE_HEADER, value = AccidentType.INCIDENT.text
        )
      )

      items.addAll(incidents.toAccidentItems())
    }

    if (requests.isNotEmpty()) {
      items.add(
        AccidentItem(
          type = AccidentsAdapter.TYPE_HEADER, value = AccidentType.REQUEST.text
        )
      )

      items.addAll(requests.toAccidentItems())
    }

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
    binding = CustomerIncidentsRequestsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@CustomerIncidentsRequests
      viewModel = this@CustomerIncidentsRequests.accidentViewModel
      accidentsAdapter = this@CustomerIncidentsRequests.accidentsAdapter
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

  private val accidentsIds: List<String>
    get() {
      val profile = userViewModel.profile.value!!
      val userAccidents = profile.accidentsIds.toMutableList()
      val divisionAccidents = profile.divisionLocal?.accidentsIds ?: listOf()
      userAccidents.addAll(divisionAccidents)
      return userAccidents.toSet().toMutableList()
    }

  private fun reloadAccidents() {
    if (hasLoads) return

    accidentViewModel.loadAccidents(accidentsIds)
  }

  override fun onResume() {
    super.onResume()

    showMenu()
    if (accidentViewModel.accidents.value!!.isEmpty()) reloadAccidents()

    userViewModel.work.observe(this, workObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.externalAction.observe(this, externalActionObserver)

    accidentViewModel.work.observe(this, workObserver)
    accidentViewModel.error.observe(this, errorObserver)
    accidentViewModel.filteredAccidents.observe(this, accidentsObserver)
  }

  override fun onStop() {
    super.onStop()

    userViewModel.work.removeObserver(workObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)

    accidentViewModel.work.removeObserver(workObserver)
    accidentViewModel.error.removeObserver(errorObserver)
    accidentViewModel.filteredAccidents.removeObserver(accidentsObserver)
  }

  fun openBottomSheetMenu() {
    if (hasLoads) {
      showLoadingErrorMessage()
      return
    }

    bottomSheetMenu.show(activity.supportFragmentManager, "ACCIDENTS_BOTTOM_MENU")
  }

  private val bottomSheetMenu: AccidentBottomSheetDialog by lazy {
    AccidentBottomSheetDialog(bottomSheetMenuListener)
  }
  private val bottomSheetMenuListener: AccidentBottomSheetDialog.Companion.ItemClickListener by lazy {
    object: AccidentBottomSheetDialog.Companion.ItemClickListener {
      override fun onItemClick(accidentType: AccidentType) {
        when(accidentType) {
          AccidentType.REQUEST -> showDialogAddRequest()
          AccidentType.INCIDENT -> showDialogAddIncident()
        }
        bottomSheetMenu.dismiss()
      }
    }
  }

  private val addAccidentDialogListener by lazy {
    object: AddAccidentDialog.Companion.Listener {
      override fun afterAdded(accident: Accident) {
        accidentViewModel.addAccident(accident)
        userViewModel.afterAccident(accident)
        addAccidentDialog.dismiss()
      }
    }
  }

  private val addAccidentDialog: AddAccidentDialog by lazy {
    AddAccidentDialog(requireContext(), this, accidentViewModel, addAccidentDialogListener)
  }

  fun showDialogAddIncident() {
    addAccidentDialog.open(userViewModel.profile.value!!, AccidentType.INCIDENT)
  }

  fun showDialogAddRequest() {
    addAccidentDialog.open(userViewModel.profile.value!!, AccidentType.REQUEST)
  }
}