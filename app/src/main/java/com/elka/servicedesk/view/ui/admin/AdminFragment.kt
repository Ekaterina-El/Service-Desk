package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AdminFragmentBinding
import com.elka.servicedesk.databinding.ManagerFragmentBinding
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel

class AdminFragment : AdminBaseFragment() {
  private lateinit var binding: AdminFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = AdminFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@AdminFragment.adminViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.adminContainer) as NavHostFragment)
        .navController
    binding.bottomMenu.setupWithNavController(navController)
  }
}