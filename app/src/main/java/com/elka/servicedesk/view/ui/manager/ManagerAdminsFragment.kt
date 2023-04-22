package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ManagerAdminsFragmentBinding
import com.elka.servicedesk.databinding.WelcomeFragmentBinding
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.dialog.InformDialog
import com.elka.servicedesk.view.dialog.RegistrationAdminDialog
import com.elka.servicedesk.view.ui.UserBaseFragment

class ManagerAdminsFragment : ManagerBaseFragment() {
  private lateinit var binding: ManagerAdminsFragmentBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerAdminsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ManagerAdminsFragment
      viewModel = this@ManagerAdminsFragment.adminViewModel
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (adminViewModel.admins.value!!.isEmpty()) adminViewModel.loadAdmins()
  }

  override fun onResume() {
    super.onResume()
    adminViewModel.work.observe(this, workObserver)
    adminViewModel.error.observe(this, errorObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.work.removeObserver(workObserver)
    adminViewModel.error.removeObserver(errorObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.work.removeObserver(workObserver)
  }


  private val regAdminDialogListener by lazy {
    object : RegistrationAdminDialog.Companion.Listener {
      override fun afterAdded(user: User, password: String) {
        regAdminDialog.disagree()
        showAdminCredentials(user, password)
      }
    }
  }

  private val adminCredentialsDialogListener by lazy {
    object : InformDialog.Companion.Listener {
      override fun copyMessage(message: String) {
        copyToClipboard(message)
      }
    }
  }

  fun showAdminCredentials(user: User, password: String) {
    val title = getString(R.string.admin_added)
    val message = getString(R.string.admin_auth_data, user.email, password)
    val hint = getString(R.string.admin_added_hint)

    activity.informDialog.open(
      title,
      message,
      hint,
      adminCredentialsDialogListener,
      "${user.email} | $password"
    )
  }

  private val regAdminDialog: RegistrationAdminDialog by lazy {
    RegistrationAdminDialog(
      requireContext(),
      viewLifecycleOwner,
      regAdminDialogListener
    )
  }

  fun openRegAdminDialog() {
    val credentials = getCredentials() ?: return
    regAdminDialog.open(adminViewModel, credentials.email, credentials.password)
  }
}