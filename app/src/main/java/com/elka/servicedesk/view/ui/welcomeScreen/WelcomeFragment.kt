package com.elka.servicedesk.view.ui.welcomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel

class WelcomeFragment : BaseFragment() {
  private lateinit var binding: WelcomeFragmentBinding
  private val divisionsViewModel by activityViewModels<DivisionsViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = WelcomeFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@WelcomeFragment
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (divisionsViewModel.divisions.value!!.isEmpty()) divisionsViewModel.loadDivisions()
  }

  override fun onResume() {
    super.onResume()
    divisionsViewModel.work.observe(this, workObserver)
    divisionsViewModel.error.observe(this, errorObserver)
  }

  override fun onStop() {
    super.onStop()
    divisionsViewModel.work.removeObserver(workObserver)
    divisionsViewModel.error.removeObserver(errorObserver)
  }

  fun goAuth() {
//        navController.navigate(R.id.action_welcomeFragment_to_authFragment)
  }

  fun goRegistration() {
        navController.navigate(R.id.action_welcomeFragment_to_registrationFragment)
  }
}