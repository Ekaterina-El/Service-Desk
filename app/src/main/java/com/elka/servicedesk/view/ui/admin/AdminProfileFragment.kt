package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.elka.servicedesk.databinding.AdminProfileFragmentBinding
import com.elka.servicedesk.other.Action

class AdminProfileFragment : AdminBaseFragment() {
  private lateinit var binding: AdminProfileFragmentBinding

  override val externalActionObserver = Observer<Action?> {
    if (it == null) return@Observer
    super.externalActionObserver.onChanged(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = AdminProfileFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AdminProfileFragment
      viewModel = this@AdminProfileFragment.userViewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    userViewModel.error.observe(this, errorObserver)
    userViewModel.externalAction.observe(this, externalActionObserver)

    userViewModel.work.observe(this, workObserver)
    engineersViewModel.work.observe(this, workObserver)
    divisionsViewModel.work.observe(this, workObserver)
    accidentViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)

    userViewModel.work.removeObserver(workObserver)
    engineersViewModel.work.removeObserver(workObserver)
    divisionsViewModel.work.removeObserver(workObserver)
    accidentViewModel.work.removeObserver(workObserver)
  }
}

