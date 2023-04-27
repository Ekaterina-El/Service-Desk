package com.elka.servicedesk.view.ui.analyst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AnalystFragmentBinding
import com.elka.servicedesk.databinding.CustomerFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment

class AnalystFragment: AnalystBaseFragment() {
  private lateinit var binding: AnalystFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = AnalystFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@AnalystFragment.analystViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.analystContainer) as NavHostFragment)
        .navController
    binding.bottomMenu.setupWithNavController(navController)
  }
}