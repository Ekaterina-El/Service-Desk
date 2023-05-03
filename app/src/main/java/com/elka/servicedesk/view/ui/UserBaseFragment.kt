package com.elka.servicedesk.view.ui

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.elka.servicedesk.R
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.view.dialog.ConfirmDialog
import com.elka.servicedesk.view.ui.admin.AdminFailAccidentsFragmentDirections
import com.elka.servicedesk.view.ui.engineer.EngineerAccidentsFragmentDirections
import com.elka.servicedesk.view.ui.customer.CustomerIncidentsRequestsDirections
import com.elka.servicedesk.view.ui.engineer.EngineerAccidentsInWorkFragmentDirections
import com.elka.servicedesk.viewModel.UserViewModel

abstract class UserBaseFragment: BaseFragment() {

  fun goAccident(accidentId: String, path: Int) {
    val dir = when(path) {
      FROM_ACTIVE_ACCIDENTS_TO_ACCIDENT -> EngineerAccidentsFragmentDirections. actionEngineerAccidentsFragmentToAccidentFragment(accidentId)
      FROM_IN_WORK_ACCIDENTS_TO_ACCIDENT -> EngineerAccidentsInWorkFragmentDirections.actionEngineerAccidentsInWorkFragmentToAccidentFragment(accidentId)
      FROM_CUSTOMER_ACCIDENTS_TO_ACCIDENT -> CustomerIncidentsRequestsDirections.actionCustomerIncidentsRequestsToAccidentFragment2(accidentId)
      FROM_FAIL_ACCIDENTS_TO_ACCIDENT -> AdminFailAccidentsFragmentDirections.actionAdminFailAccidentsFragmentToAccidentFragment2(accidentId)
      else -> return
    }

    try {
      navController.navigate(dir)
    } catch (e: java.lang.Exception) {
      Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
    }
  }

  companion object {
    const val FROM_ACTIVE_ACCIDENTS_TO_ACCIDENT = 1
    const val FROM_IN_WORK_ACCIDENTS_TO_ACCIDENT = 2
    const val FROM_CUSTOMER_ACCIDENTS_TO_ACCIDENT = 3
    const val FROM_FAIL_ACCIDENTS_TO_ACCIDENT = 4
  }

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

  fun logoutWithoutConfirm() {
    setCredentials(null)
    userViewModel.logout()
  }
}