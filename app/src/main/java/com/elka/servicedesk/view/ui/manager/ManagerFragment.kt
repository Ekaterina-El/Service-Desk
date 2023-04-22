package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ManagerFragmentBinding
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel

class ManagerFragment : BaseFragment() {
  private lateinit var binding: ManagerFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.managerContainer) as NavHostFragment)
        .navController
    binding.bottomMenu.setupWithNavController(navController)
  }
}