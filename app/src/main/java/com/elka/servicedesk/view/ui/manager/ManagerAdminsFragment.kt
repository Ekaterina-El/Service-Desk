package com.elka.servicedesk.view.ui.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ManagerAdminsFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.dialog.ConfirmDialog
import com.elka.servicedesk.view.dialog.InformDialog
import com.elka.servicedesk.view.dialog.RegistrationAdminDialog
import com.elka.servicedesk.view.list.users.UsersAdapter
import com.elka.servicedesk.view.list.users.UsersViewHolder

class ManagerAdminsFragment : ManagerBaseFragment() {
  private lateinit var binding: ManagerAdminsFragmentBinding

  private val adminsAdapter by lazy {
    UsersAdapter(object : UsersViewHolder.Companion.Listener {
      override fun onSelect(user: User) {}

      override fun onBlock(user: User) {
        openConfirmDeleteDialog(user)
      }

      override fun onChangeDivisions(user: User) {}
    })
  }

  private val adminsObserver = Observer<List<User>> {
    adminsAdapter.setItems(it)
  }

  override val workObserver = Observer<List<Work>> {
    binding.swiper1.isRefreshing = hasLoads
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = ManagerAdminsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@ManagerAdminsFragment
      viewModel = this@ManagerAdminsFragment.adminViewModel
      adminsAdapter = this@ManagerAdminsFragment.adminsAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.adminsList.addItemDecoration(decorator)

    val refresherColor = requireContext().getColor(R.color.accent)
    val swipeRefreshListener =
      SwipeRefreshLayout.OnRefreshListener { adminViewModel.loadUsers() }

    binding.swiper1.setColorSchemeColors(refresherColor)
    binding.swiper1.setOnRefreshListener(swipeRefreshListener)

    binding.layoutNoFound.message.text = getString(R.string.list_empty)
  }

  override fun onResume() {
    super.onResume()

    if (adminViewModel.users.value!!.isEmpty()) adminViewModel.loadUsers()

    adminViewModel.work.observe(this, workObserver)
    adminViewModel.error.observe(this, errorObserver)
    adminViewModel.filteredUsers.observe(this, adminsObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()
    adminViewModel.work.removeObserver(workObserver)
    adminViewModel.error.removeObserver(errorObserver)
    adminViewModel.filteredUsers.removeObserver(adminsObserver)

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
    val hint = getString(R.string.user_added_hint)

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
    if (hasLoads) {
      showLoadingErrorMessage()
      return
    }

    val credentials = getCredentials() ?: return
    regAdminDialog.open(
      adminViewModel,
      credentials.email,
      credentials.password,
      userViewModel.profile.value!!
    )
  }

  private fun openConfirmDeleteDialog(admin: User) {
    val title = getString(R.string.block_admin_title)
    val message = getString(R.string.block_admin_message, admin.fullName)
    val listener = object : ConfirmDialog.Companion.Listener {
      override fun agree() {
        adminViewModel.blockUser(admin, userViewModel.profile.value!!)
        confirmDialog.close()
      }

      override fun disagree() {
        confirmDialog.close()
      }
    }

    confirmDialog.open(title, message, listener)
  }
}