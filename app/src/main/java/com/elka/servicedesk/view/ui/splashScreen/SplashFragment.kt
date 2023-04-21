package com.elka.servicedesk.view.ui.splashScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.SplashFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Constants
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.SplashViewModel

class SplashFragment : UserBaseFragment() {
  private lateinit var binding: SplashFragmentBinding
  private lateinit var viewModel: SplashViewModel

  private val handler by lazy { Handler(Looper.getMainLooper()) }

  override val externalActionObserver = Observer<Action?> { action ->
    if (action == null) return@Observer
    when (action) {
      Action.GO_LOGIN -> goLogin()
      Action.RESTART -> restartApp()
      Action.GO_PROFILE -> userViewModel.loadCurrentUserProfile()
      else -> Unit
    }
  }

  private val profileObserver = Observer<User?> {
    if (it == null) return@Observer
    goProfile(it)
  }

  private fun goProfile(user: User) {
    val dirs = SplashFragmentDirections
    val dir = when(user.role) {
      Role.USER -> dirs.actionSplashFragmentToUserProfileFragment()
      Role.ANALYST -> dirs.actionSplashFragmentToAnalystProfileFragment()
      Role.ADMIN -> dirs.actionSplashFragmentToAdminProfileFragment()
      Role.MANAGER -> dirs.actionSplashFragmentToManagerProfileFragment()
    }
    navController.navigate(dir)
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

  override fun onStart() {
    super.onStart()
    viewModel.checkLoginStatus()
  }

  override fun onResume() {
    super.onResume()

    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.error.observe(viewLifecycleOwner, errorObserver)

    userViewModel.error.observe(viewLifecycleOwner, errorObserver)
    userViewModel.profile.observe(viewLifecycleOwner, profileObserver)
    userViewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.error.removeObserver(errorObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.profile.removeObserver(profileObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)
  }
}