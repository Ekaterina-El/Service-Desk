package com.elka.servicedesk.view.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.elka.servicedesk.databinding.CustomerProfileFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.dialog.ChangeUserDivisionDialog
import com.elka.servicedesk.viewModel.DivisionsViewModel

class CustomerProfileFragment : CustomerBaseFragment() {
  private val divisionsViewModel by activityViewModels<DivisionsViewModel>()
  private lateinit var binding: CustomerProfileFragmentBinding

  override val externalActionObserver = Observer<Action?> {
    if (it == null) return@Observer
    super.externalActionObserver.onChanged(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = CustomerProfileFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@CustomerProfileFragment
      viewModel = this@CustomerProfileFragment.userViewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    divisionsViewModel.loadDivisions()

    userViewModel.work.observe(this, workObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.externalAction.observe(this, externalActionObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.work.removeObserver(workObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)
  }

  private val changeUserDivisionDialogListener: ChangeUserDivisionDialog.Companion.Listener by lazy {
    object : ChangeUserDivisionDialog.Companion.Listener {
      override fun onSave(division: Division) {
        userViewModel.changeDivision(division)
        changeUserDivisionDialog.disagree()
      }
    }
  }
  private val changeUserDivisionDialog: ChangeUserDivisionDialog by lazy {
    ChangeUserDivisionDialog(requireContext(), this, changeUserDivisionDialogListener)
  }
  
  fun changeDivision() {
    val divisions = divisionsViewModel.divisions.value!!
    changeUserDivisionDialog.open(userViewModel.profile.value!!.divisionLocal, divisions)
  }
}

