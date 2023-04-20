package com.elka.servicedesk.view.ui.splashScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.SplashFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Constants
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel
import com.elka.servicedesk.viewModel.SplashViewModel

class SplashFragment : BaseFragment() {
  private lateinit var binding: SplashFragmentBinding
  private lateinit var viewModel: SplashViewModel

  private val handler by lazy { Handler(Looper.getMainLooper()) }

  private val externalActionObserver = Observer<Action?> { action ->
    if (action == null) return@Observer
    when (action) {
      Action.GO_LOGIN -> goLogin()
//      Action.GO_ORGANIZATION -> organizationViewModel.loadProfile()
      Action.RESTART -> restartApp()
      else -> Unit
    }
  }

  private fun goOrganization() {
//    val direction = SplashFragmentDirections.actionSplashFragmentToOrganizationFragment2()
//    findNavController().navigate(direction)
  }

  private fun goLogin() {
    handler.postDelayed({
      try {
        val direction = SplashFragmentDirections.actionSplashFragmentToWelcomeFragment()
        findNavController().navigate(direction)
      } catch (_: Exception) {
      }
    }, Constants.LOAD_DELAY)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
    requireActivity().window.statusBarColor = requireContext().getColor(R.color.white)
    binding = SplashFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()

    goLogin()
    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.error.observe(viewLifecycleOwner, errorObserver)
//    organizationViewModel.error.observe(viewLifecycleOwner, errorObserver)
//    organizationViewModel.profile.observe(viewLifecycleOwner, profileObserver)
//    organizationViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.error.removeObserver(errorObserver)
//    organizationViewModel.error.removeObserver(errorObserver)
//    organizationViewModel.profile.removeObserver(profileObserver)
//    organizationViewModel.externalAction.removeObserver(externalActionObserver)
  }
}