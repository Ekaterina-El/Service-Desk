package com.elka.servicedesk.view.ui.welcomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.view.ui.BaseFragment

class WelcomeFragment : BaseFragment() {
  private lateinit var binding: WelcomeFragmentBinding

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

  fun goAuth() {
//        navController.navigate(R.id.action_welcomeFragment_to_authFragment)
  }

  fun goRegistration() {
        navController.navigate(R.id.action_welcomeFragment_to_registrationFragment)
  }
}