package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.elka.servicedesk.databinding.AnalystProfileFragmentBinding
import com.elka.servicedesk.databinding.ManagerProfileFragmentBinding
import com.elka.servicedesk.databinding.UserProfileFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.view.ui.UserBaseFragment

class ManagerProfileFragment : UserBaseFragment() {
  private lateinit var binding: ManagerProfileFragmentBinding

  override val externalActionObserver = Observer<Action?> {
    if (it == null) return@Observer
    super.externalActionObserver.onChanged(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerProfileFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ManagerProfileFragment
      viewModel = this@ManagerProfileFragment.userViewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
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
}

