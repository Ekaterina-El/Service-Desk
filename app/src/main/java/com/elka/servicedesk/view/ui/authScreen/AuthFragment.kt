package com.elka.servicedesk.view.ui.authScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elka.servicedesk.databinding.AuthFragmentBinding
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AuthViewModel

class AuthFragment : UserBaseFragment() {
  private lateinit var binding: AuthFragmentBinding
  private lateinit var viewModel: AuthViewModel

  private var fieldErrorsObserver = Observer<List<FieldError>> { errors ->
    showErrors(errors, fields)
  }

  override val externalActionObserver = Observer<Action?> {
    if (it == null) return@Observer

    when (it) {
      Action.LOAD_PROFILE -> userViewModel.loadCurrentUserProfile()
      else -> Unit
    }
  }

  private val profileObserver = Observer<User?> {
    if (it == null) return@Observer

    navigateToUserScreen(it)
  }

  private fun navigateToUserScreen(user: User) {
    setCredentials(
      Credentials(
        viewModel.email.value!!,
        viewModel.password.value!!
      )
    )
    viewModel.clear()

    val dirs = AuthFragmentDirections
    val dir = when (user.role) {
      Role.USER -> dirs.actionAuthFragmentToUserProfileFragment()
      Role.ANALYST -> dirs.actionAuthFragmentToAnalystProfileFragment()
      Role.ADMIN -> dirs.actionAuthFragmentToAdminProfileFragment()
      Role.MANAGER -> dirs.actionAuthFragmentToManagerFragment()
    }
    navController.navigate(dir)
    Toast.makeText(requireContext(), "Logined", Toast.LENGTH_SHORT).show()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

    binding = AuthFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AuthFragment
      viewModel = this@AuthFragment.viewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    viewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
    viewModel.error.observe(viewLifecycleOwner, errorObserver)
    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.work.observe(viewLifecycleOwner, workObserver)

    userViewModel.profile.observe(viewLifecycleOwner, profileObserver)
    userViewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onStop() {
    super.onStop()
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.error.removeObserver(errorObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.work.removeObserver(workObserver)

    userViewModel.profile.removeObserver(profileObserver)
    userViewModel.work.removeObserver(workObserver)
  }

  fun goBack() {
    navController.popBackStack()
    viewModel.clear()
  }

  fun tryAuth() {
    viewModel.tryAuth()
  }


  private val fields by lazy {
    hashMapOf<Field, Any>(
      Pair(Field.PASSWORD, binding.layoutPassword),
      Pair(Field.EMAIL, binding.layoutEmail),
    )
  }
}