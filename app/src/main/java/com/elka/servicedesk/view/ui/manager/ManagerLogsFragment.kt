package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ManagerLogsFragmentBinding
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel

class ManagerLogsFragment : BaseFragment() {
  private lateinit var binding: ManagerLogsFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerLogsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ManagerLogsFragment
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
  }

  override fun onStop() {
    super.onStop()
  }
}