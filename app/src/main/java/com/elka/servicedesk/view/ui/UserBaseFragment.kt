package com.elka.servicedesk.view.ui

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.elka.servicedesk.R
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.view.dialog.ConfirmDialog
import com.elka.servicedesk.viewModel.UserViewModel

abstract class UserBaseFragment: BaseFragment() {

  protected val userViewModel by activityViewModels<UserViewModel>()

  protected open val externalActionObserver = Observer<Action?> { action ->
    if (action == Action.RESTART) restartApp()
  }

  protected val confirmDialog by lazy { ConfirmDialog(requireContext()) }

  private val exitListener by lazy {
    object: ConfirmDialog.Companion.Listener {
      override fun agree() {
        confirmDialog.close()
        setCredentials(null)
        userViewModel.logout()
      }

      override fun disagree() {
        confirmDialog.close()
      }
    }
  }

  fun logout() {
    val title = getString(R.string.exit_title)
    val message = getString(R.string.exit_message)
    confirmDialog.open(title, message, exitListener)
  }

}