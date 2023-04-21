package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ManagerProfileFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.dialog.InformDialog
import com.elka.servicedesk.view.dialog.RegistrationAdminDialog
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AdminsViewModel

class ManagerProfileFragment : UserBaseFragment() {
  private lateinit var binding: ManagerProfileFragmentBinding
  private val adminViewModel by activityViewModels<AdminsViewModel>()

  override val externalActionObserver = Observer<Action?> {
    if (it == null) return@Observer
    super.externalActionObserver.onChanged(it)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerProfileFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ManagerProfileFragment
      viewModel = this@ManagerProfileFragment.userViewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    userViewModel.work.observe(this, workObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.externalAction.observe(this, externalActionObserver)

    if (adminViewModel.admins.value!!.isEmpty()) adminViewModel.loadAdmins()
  }

  override fun onStop() {
    super.onStop()
    userViewModel.work.removeObserver(workObserver)
    userViewModel.error.removeObserver(errorObserver)
    userViewModel.externalAction.removeObserver(externalActionObserver)
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

