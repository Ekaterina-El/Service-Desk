package com.elka.servicedesk.view.ui.accident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AccidentFragmentBinding
import com.elka.servicedesk.other.AccidentMoreInfo
import com.elka.servicedesk.other.AccidentStatus
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.dialog.AddMoreInfoDialog
import com.elka.servicedesk.view.dialog.ChangeEngineerDialog
import com.elka.servicedesk.view.list.images.ImageItem
import com.elka.servicedesk.view.list.images.ImagesAdapter
import com.elka.servicedesk.view.list.logs.LogsAdapter
import com.elka.servicedesk.view.list.moreInfo.MoreInfoAdapter
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel
import com.elka.servicedesk.viewModel.EngineersViewModel

class AccidentFragment : UserBaseFragment() {
	private lateinit var binding: AccidentFragmentBinding
	private val accidentsViewModel by activityViewModels<AccidentsViewModel>()
	private val engineersViewModel by activityViewModels<EngineersViewModel>()

	private val moreInfoAdapter by lazy { MoreInfoAdapter() }

	private val logsAdapter by lazy { LogsAdapter() }

	private val accidentLogsObserver = Observer<List<Log>> {
		logsAdapter.setItems(it)
	}


	private val imagesAdapter: ImagesAdapter by lazy {
		ImagesAdapter(null, null)
	}

	private val accidentObserver = Observer<Accident?> { accident ->
		if (accident == null) return@Observer

		updateMenuStatus()

		val items =
			accident.photosURL.map { ImageItem(type = ImagesAdapter.TYPE_ITEM, it) }.toMutableList()
		imagesAdapter.setItems(items)
		moreInfoAdapter.setItems(accident.moreInfo)
	}

	private val works = listOf(
		Work.LOAD_ACCIDENT,
		Work.ACCEPT_ACCIDENT_TO_WORK,
		Work.CLOSE_ACCIDENT,
		Work.ADD_MORE_INFORMATION,
		Work.EXCALATION, Work.LOAD_USERS,
		Work.CHANGE_ENGINEER
	)

	private val hasLoads: Boolean
		get() {
			val w1 = userViewModel.work.value!!.toMutableList()
			val w2 = accidentsViewModel.work.value!!
			val w3 = engineersViewModel.work.value!!
			w1.addAll(w2)
			w1.addAll(w3)
			return getHasLoads(w1, works)
		}

	override val workObserver = Observer<List<Work>> {
		binding.swiper.isRefreshing = hasLoads
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = AccidentFragmentBinding.inflate(layoutInflater, container, false)
		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			master = this@AccidentFragment
			viewModel = this@AccidentFragment.accidentsViewModel
			imagesAdapter = this@AccidentFragment.imagesAdapter
			logsAdapter = this@AccidentFragment.logsAdapter
			moreInfoAdapter = this@AccidentFragment.moreInfoAdapter
		}

		return binding.root
	}

	fun goBack() {
		accidentsViewModel.clearCurrentAccident()
		navController.popBackStack()
	}

	private val onBackPressedCallback by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				goBack()
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// get args
		val args = AccidentFragmentArgs.fromBundle(requireArguments())
		accidentsViewModel.loadCurrentOpenAccident(args.accidentId)

		// add decorator to list
		val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
		binding.logsList.addItemDecoration(decorator)

		// add on back press listener
		activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)

		// work with swipe refresher
		val refresherColor = requireContext().getColor(R.color.accent)
		val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener { reloadAccident() }

		binding.swiper.setColorSchemeColors(refresherColor)
		binding.swiper.setOnRefreshListener(swipeRefreshListener)
	}

	private fun reloadAccident() {
		accidentsViewModel.reloadCurrentAccident()
		engineersViewModel.loadUsers()
	}

	override fun onResume() {
		super.onResume()

		userViewModel.error.observe(this, errorObserver)
		userViewModel.work.observe(this, workObserver)
		accidentsViewModel.error.observe(this, errorObserver)
		accidentsViewModel.work.observe(this, workObserver)
		accidentsViewModel.currentAccident.observe(this, accidentObserver)
		accidentsViewModel.currentAccidentLogs.observe(this, accidentLogsObserver)
	}

	override fun onStop() {
		super.onStop()

		userViewModel.error.removeObserver(errorObserver)
		userViewModel.work.removeObserver(workObserver)
		accidentsViewModel.error.removeObserver(errorObserver)
		accidentsViewModel.work.removeObserver(workObserver)
		accidentsViewModel.currentAccident.removeObserver(accidentObserver)
		accidentsViewModel.currentAccidentLogs.removeObserver(accidentLogsObserver)
	}

	private val menu by lazy {
		val popupMenu = PopupMenu(context, binding.menu)
		popupMenu.setOnMenuItemClickListener {
			when (it.itemId) {
				ACCEPT_ACCIDENT_TO_WORK -> acceptAccidentToWork()
				CLOSE_ACCIDENT -> closeAccident()
				ACCEPT_CLOSE_ACCIDENT -> acceptCloseAccidentByUser()
				DENY_CLOSE_ACCIDENT, ESCALATION -> excalactionAccident()
				CHANGE_ENGINEER -> changeEngineer()
				ADD_INFORMATION, WAIT_MORE_INFORMATION -> showDialogSendRequestToAddMoreInformation()
				else -> Unit
			}
			return@setOnMenuItemClickListener true
		}

		return@lazy popupMenu
	}

	private fun updateMenuStatus() {
		val role = userViewModel.profile.value!!.role
		val accidentStatus = accidentsViewModel.currentAccident.value!!.status

		val viewStatus = when (role) {
			Role.ADMIN -> {
				when(accidentStatus) {
					AccidentStatus.ESCALATION -> View.VISIBLE
					else -> View.GONE
				}
			}
			Role.USER -> {
				when (accidentStatus) {
					AccidentStatus.CLOSED,
					AccidentStatus.WAITING,
					-> View.VISIBLE
					else -> View.INVISIBLE
				}
			}
			Role.ENGINEER -> {
				when (accidentStatus) {
					AccidentStatus.ACTIVE, AccidentStatus.IN_WORK -> View.VISIBLE
					else -> View.INVISIBLE
				}
			}
			else -> View.INVISIBLE
		}

		binding.menu.visibility = viewStatus
	}

	private fun inflateMenu() {
		val menu = menu.menu
		menu.clear()

		val role = userViewModel.profile.value!!.role
		val accidentStatus = accidentsViewModel.currentAccident.value!!.status

		when (role) {
			Role.USER -> {
				when (accidentStatus) {
					AccidentStatus.CLOSED -> {
						menu.add(0, ACCEPT_CLOSE_ACCIDENT, 0, R.string.accept_close_accident)
						menu.add(0, DENY_CLOSE_ACCIDENT, 0, R.string.deny_close_accident)
					}
					AccidentStatus.WAITING -> {
						menu.add(0, ADD_INFORMATION, 0, R.string.add_information)
					}
					else -> Unit
				}
			}
			Role.ENGINEER -> {
				when (accidentStatus) {
					AccidentStatus.ACTIVE -> {
						menu.add(0, ACCEPT_ACCIDENT_TO_WORK, 0, R.string.accept_accident_to_work)
					}
					AccidentStatus.IN_WORK -> {
						menu.add(0, WAIT_MORE_INFORMATION, 0, R.string.wait_more_information)
						menu.add(0, CLOSE_ACCIDENT, 0, R.string.close_accident)
						menu.add(0, ESCALATION, 0, R.string.excalation)
					}
					else -> Unit
				}
			}
			Role.ADMIN -> {
				when (accidentStatus) {
					AccidentStatus.ESCALATION -> {
						menu.add(0, CHANGE_ENGINEER, 0, R.string.change_engineer)
					}
					else -> Unit
				}
			}
			else -> Unit
		}

	}

	fun openMenu() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		inflateMenu()
		menu.show()
	}

	private fun acceptAccidentToWork() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		val engineer = userViewModel.profile.value!!.copy()
		accidentsViewModel.acceptCurrentAccidentToWork(engineer) {
			Toast.makeText(
				requireContext(), getString(R.string.accident_was_accdepted_to_work), Toast.LENGTH_SHORT
			).show()
		}
	}

	private fun closeAccident() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		accidentsViewModel.closeAccident {
			Toast.makeText(requireContext(), getString(R.string.accident_was_close), Toast.LENGTH_SHORT)
				.show()
		}
	}

	private fun acceptCloseAccidentByUser() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		val user = userViewModel.profile.value!!.copy()
		accidentsViewModel.acceptCloseAccidentFromUser(user) {
			Toast.makeText(
				requireContext(), getString(R.string.accident_was_close_by_user), Toast.LENGTH_SHORT
			).show()
		}
	}

	private var isEscalation = false
	private val addMoreInfoDialogListener by lazy {
		object : AddMoreInfoDialog.Companion.Listener {
			override fun onSave(accidentMoreInfo: AccidentMoreInfo) {
				addMoreInfoDialog.disagree()
				if (isEscalation) sendEscalation(accidentMoreInfo) else sendAddMore(accidentMoreInfo)
				isEscalation = false
			}
		}
	}

	private fun sendEscalation(accidentMoreInfo: AccidentMoreInfo) {
		accidentsViewModel.sendExcalation(accidentMoreInfo) {
			Toast.makeText(
				requireContext(),
				getString(R.string.request_to_excalation_sent),
				Toast.LENGTH_SHORT
			).show()
		}
	}

	private fun sendAddMore(accidentMoreInfo: AccidentMoreInfo) {
		accidentsViewModel.addAccidentMoreInfo(accidentMoreInfo) {
			val strRes =
				if (accidentMoreInfo.user!!.role == Role.USER) R.string.more_information_added else R.string.request_to_add_more_information_added
			Toast.makeText(requireContext(), getString(strRes), Toast.LENGTH_SHORT).show()
		}
	}

	private val addMoreInfoDialog: AddMoreInfoDialog by lazy {
		AddMoreInfoDialog(requireContext(), addMoreInfoDialogListener)
	}

	private fun excalactionAccident() {
		isEscalation = true
		val title = getString(R.string.excalation)
		val hint = getString(R.string.excalation_hint)
		val user = userViewModel.profile.value!!
		addMoreInfoDialog.open(user, title, hint)
	}

	private fun showDialogSendRequestToAddMoreInformation() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		val user = userViewModel.profile.value!!

		val strRes = when (user.role) {
			Role.USER -> listOf(
				R.string.user_add_more_information_title,
				R.string.user_add_more_information_hint,
			)
			Role.ENGINEER -> listOf(
				R.string.engineer_add_more_information_title, R.string.engineer_add_more_information_hint
			)
			else -> null
		} ?: return

		val title = getString(strRes[0])
		val hint = getString(strRes[1])
		addMoreInfoDialog.open(user, title, hint)
	}


	private val changeEngineerDialogListener by lazy {
		object : ChangeEngineerDialog.Companion.Listener {
			override fun onChange(engineer: User) {
				val admin = userViewModel.profile.value!!.copy()
				accidentsViewModel.changeEngineer(engineer, admin)
				changeEngineerDialog.disagree()
			}
		}
	}
	private val changeEngineerDialog: ChangeEngineerDialog by lazy { ChangeEngineerDialog(requireContext(), changeEngineerDialogListener) }
	private fun changeEngineer() {
		val currentEngineerId = accidentsViewModel.currentAccident.value!!.engineerId
		val engineersWithoutCurrent = engineersViewModel.users.value!!.filter { it.id != currentEngineerId }

		if (engineersWithoutCurrent.isEmpty()) {
			Toast.makeText(requireContext(), R.string.one_engineer, Toast.LENGTH_SHORT).show()
		} else {
			changeEngineerDialog.open(engineersWithoutCurrent)
		}
	}

	companion object {
		const val ACCEPT_ACCIDENT_TO_WORK = 1
		const val CLOSE_ACCIDENT = 2
		const val ACCEPT_CLOSE_ACCIDENT = 3
		const val DENY_CLOSE_ACCIDENT = 4
		const val ESCALATION = 5
		const val ADD_INFORMATION = 6
		const val WAIT_MORE_INFORMATION = 7
		const val CHANGE_ENGINEER = 8
	}
}