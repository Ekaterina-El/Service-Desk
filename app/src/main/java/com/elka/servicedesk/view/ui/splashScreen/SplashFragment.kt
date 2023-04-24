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
import com.elka.servicedesk.other.*
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
      Role.USER -> dirs.actionSplashFragmentToCustomerFragment()
      Role.ANALYST -> dirs.actionSplashFragmentToAnalystProfileFragment()
      Role.ADMIN -> dirs.actionSplashFragmentToAdminFragment()
      Role.MANAGER -> dirs.actionSplashFragmentToManagerFragment()
    }
    navController.navigate(dir)
  }

  override val errorObserver = Observer<ErrorApp?> {
    if (it == null) return@Observer
    if (it == Errors.userWasBlocked) { showDialogAboutBlocked() }
  }

  private fun showDialogAboutBlocked() {
    val title = getString(R.string.account_have_been_blocked)
    val message = getString(R.string.account_have_been_blocked_message_with_exit)
    activity.informDialog.open(
      title,
      message,
      onButtonListener = { logoutWithoutConfirm() },
      cancelable = false
    )
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