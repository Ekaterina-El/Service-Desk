package com.elka.servicedesk.view.ui.engineer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.EngineerFragmentBinding

class EngineerFragment: EngineerBaseFragment() {
  private lateinit var binding: EngineerFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = EngineerFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@EngineerFragment.engineerViewModel
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController =
      (childFragmentManager.findFragmentById(R.id.engineerContainer) as NavHostFragment)
        .navController
    binding.bottomMenu.setupWithNavController(navController)
  }
}