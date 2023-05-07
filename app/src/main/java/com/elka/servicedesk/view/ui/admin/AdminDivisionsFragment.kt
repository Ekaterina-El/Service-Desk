package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AdminDivisionsFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.dialog.AddDivisionDialog
import com.elka.servicedesk.view.dialog.ConfirmDialog
import com.elka.servicedesk.view.list.divisions.DivisionViewHolder2
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter2
import com.elka.servicedesk.viewModel.DivisionsViewModel

class AdminDivisionsFragment : AdminBaseFragment() {
  private lateinit var binding: AdminDivisionsFragmentBinding

  private val divisionsAdapter by lazy {
    DivisionsAdapter2(object : DivisionViewHolder2.Companion.Listener {
      override fun onDelete(division: Division) {
        openConfirmDeleteDialog(division)
      }
    })
  }

  private val divisionsObserver = Observer<List<Division>> {
    divisionsAdapter.setItems(it)
  }

  override val workObserver = Observer<List<Work>> {
    binding.swiper1.isRefreshing = hasLoads
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = AdminDivisionsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AdminDivisionsFragment
      viewModel = this@AdminDivisionsFragment.divisionsViewModel
      divisionsAdapter = this@AdminDivisionsFragment.divisionsAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.divisionsList.addItemDecoration(decorator)

    val refresherColor = requireContext().getColor(R.color.accent)
    val swipeRefreshListener =
      SwipeRefreshLayout.OnRefreshListener { divisionsViewModel.loadDivisions() }

    binding.swiper1.setColorSchemeColors(refresherColor)
    binding.swiper1.setOnRefreshListener(swipeRefreshListener)

    binding.layoutNoFound.message.text = getString(R.string.list_empty)
  }

  override fun onResume() {
    super.onResume()

    if (divisionsViewModel.divisions.value!!.isEmpty()) divisionsViewModel.loadDivisions()

    divisionsViewModel.error.observe(this, errorObserver)
    divisionsViewModel.filteredDivisions.observe(this, divisionsObserver)
    userViewModel.error.observe(this, errorObserver)


    userViewModel.work.observe(this, workObserver)
    engineersViewModel.work.observe(this, workObserver)
    divisionsViewModel.work.observe(this, workObserver)
    accidentViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()

    divisionsViewModel.error.removeObserver(errorObserver)
    divisionsViewModel.filteredDivisions.removeObserver(divisionsObserver)

    userViewModel.error.removeObserver(errorObserver)

    userViewModel.work.removeObserver(workObserver)
    engineersViewModel.work.removeObserver(workObserver)
    divisionsViewModel.work.removeObserver(workObserver)
    accidentViewModel.work.removeObserver(workObserver)
  }

  private val addDivisionDialog: AddDivisionDialog by lazy {
    AddDivisionDialog(requireContext(), addDivisionDialogListener)
  }

  private val addDivisionDialogListener by lazy {
    object : AddDivisionDialog.Companion.Listener {
      override fun onSave(division: Division) {
        divisionsViewModel.addDivision(division, userViewModel.profile.value!!)
        addDivisionDialog.disagree()
      }
    }
  }

  fun openAddDivisionDialog() {
    if (hasLoads) {
      showLoadingErrorMessage()
      return
    }

    addDivisionDialog.open(divisionsViewModel.divisions.value!!)
  }

  private fun openConfirmDeleteDialog(division: Division) {
    val title = getString(R.string.delete_division_title)
    val message = getString(R.string.delete_division_message, division.name)
    val listener = object : ConfirmDialog.Companion.Listener {
      override fun agree() {
        divisionsViewModel.removeDivision(division, userViewModel.profile.value!!)
        confirmDialog.close()
      }

      override fun disagree() {
        confirmDialog.close()
      }
    }

    confirmDialog.open(title, message, listener)
  }
}