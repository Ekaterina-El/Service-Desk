package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AdminEngineersFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.dialog.ChangeEngineerDivisionsDialog
import com.elka.servicedesk.view.dialog.ConfirmDialog
import com.elka.servicedesk.view.dialog.InformDialog
import com.elka.servicedesk.view.dialog.RegistrationEngineerDialog
import com.elka.servicedesk.view.list.users.UsersAdapter
import com.elka.servicedesk.view.list.users.UsersViewHolder

class AdminEngineersFragment : AdminBaseFragment() {
    private lateinit var binding: AdminEngineersFragmentBinding

    private val usersAdapter by lazy {
        UsersAdapter(object : UsersViewHolder.Companion.Listener {
            override fun onSelect(admin: User) {}

            override fun onBlock(admin: User) {
                openConfirmDeleteDialog(admin)
            }

            override fun onChangeDivisions(user: User) {
                openChangeDivisionsDialog(user)
            }
        })
    }

    private val usersObserver = Observer<List<User>> {
        usersAdapter.setItems(it)
    }

    override val workObserver = Observer<List<Work>> {
        binding.swiper1.isRefreshing = hasLoads
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = AdminEngineersFragmentBinding.inflate(layoutInflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            master = this@AdminEngineersFragment
            viewModel = this@AdminEngineersFragment.engineersViewModel
            usersAdapter = this@AdminEngineersFragment.usersAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.adminsList.addItemDecoration(decorator)

        val refresherColor = requireContext().getColor(R.color.accent)
        val swipeRefreshListener =
            SwipeRefreshLayout.OnRefreshListener { engineersViewModel.loadUsers() }

        binding.swiper1.setColorSchemeColors(refresherColor)
        binding.swiper1.setOnRefreshListener(swipeRefreshListener)

        binding.layoutNoFound.message.text = getString(R.string.list_empty)
    }

    override fun onResume() {
        super.onResume()

        divisionsViewModel.loadDivisions()
        if (engineersViewModel.users.value!!.isEmpty()) engineersViewModel.loadUsers()

        engineersViewModel.work.observe(this, workObserver)
        engineersViewModel.error.observe(this, errorObserver)
        engineersViewModel.filteredUsers.observe(this, usersObserver)
        userViewModel.error.observe(this, errorObserver)
        userViewModel.work.observe(this, workObserver)
    }

    override fun onStop() {
        super.onStop()
        engineersViewModel.work.removeObserver(workObserver)
        engineersViewModel.error.removeObserver(errorObserver)
        engineersViewModel.filteredUsers.removeObserver(usersObserver)

        userViewModel.error.removeObserver(errorObserver)
        userViewModel.work.removeObserver(workObserver)
    }

    private val regEngineerDialog: RegistrationEngineerDialog by lazy {
        RegistrationEngineerDialog(
            requireContext(), viewLifecycleOwner, engineersViewModel, regEngineerDialogListener
        )
    }

    private val regEngineerDialogListener by lazy {
        object : RegistrationEngineerDialog.Companion.Listener {
            override fun afterAdded(user: User, password: String) {
                regEngineerDialog.disagree()
                showEngineerCredentials(user, password)
            }
        }
    }

    fun showEngineerCredentials(user: User, password: String) {
        val title = getString(R.string.engineer_added)
        val message = getString(R.string.engineer_auth_data, user.email, password)
        val hint = getString(R.string.user_added_hint)

        activity.informDialog.open(
            title, message, hint, engineerCredentialsDialogListener, "${user.email} | $password"
        )
    }

    private val engineerCredentialsDialogListener by lazy {
        object : InformDialog.Companion.Listener {
            override fun copyMessage(message: String) {
                copyToClipboard(message)
            }
        }
    }


    private fun openConfirmDeleteDialog(user: User) {
        val title = getString(R.string.block_engineer_title)
        val message = getString(R.string.block_engineer_message, user.fullName)
        val listener = object : ConfirmDialog.Companion.Listener {
            override fun agree() {
                engineersViewModel.blockUser(user, userViewModel.profile.value!!) { engineerAccidents: List<Accident> ->
                    val admin = userViewModel.profile.value!!
                    val reason = getString(R.string.engineer_has_blocked)
                    accidentViewModel.sendEscalationsAfterBlockEngineer(engineerAccidents, admin, reason)
                }
                confirmDialog.close()
            }

            override fun disagree() {
                confirmDialog.close()
            }
        }

        confirmDialog.open(title, message, listener)
    }

    fun openRegEngineerDialog() {
        if (hasLoads) {
            showLoadingErrorMessage()
            return
        }

        val credentials = getCredentials() ?: return
        regEngineerDialog.open(
            credentials,
            userViewModel.profile.value!!,
            divisionsViewModel.divisions.value!!,
        )
    }

    private val changeEngineerDivisionsDialog: ChangeEngineerDivisionsDialog by lazy {
        ChangeEngineerDivisionsDialog(requireContext(), changeEngineerDivisionsDialogListener)
    }

    private val changeEngineerDivisionsDialogListener by lazy {
        object : ChangeEngineerDivisionsDialog.Companion.Listener {
            override fun onSave(user: User) {
                engineersViewModel.updateUser(user, userViewModel.profile.value!!)
                changeEngineerDivisionsDialog.disagree()
            }
        }
    }

    private fun openChangeDivisionsDialog(user: User) {
        if (hasLoads) {
            showLoadingErrorMessage()
            return
        }

        val allDivisions = divisionsViewModel.divisions.value!!
        changeEngineerDivisionsDialog.open(user, allDivisions)
    }
}