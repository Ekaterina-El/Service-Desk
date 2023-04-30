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
import com.elka.servicedesk.other.AccidentStatus
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.view.list.images.ImageItem
import com.elka.servicedesk.view.list.images.ImagesAdapter
import com.elka.servicedesk.view.list.logs.LogsAdapter
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel

class AccidentFragment : UserBaseFragment() {
	private lateinit var binding: AccidentFragmentBinding
	private val accidentsViewModel by activityViewModels<AccidentsViewModel>()

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
	}

	private val works = listOf(
		Work.LOAD_ACCIDENT, Work.ACCPET_ACCIDENT_TO_WORK, Work.CLOSE_ACCIDENT
	)

	private val hasLoads: Boolean
		get() {
			val w1 = userViewModel.work.value!!.toMutableList()
			val w2 = accidentsViewModel.work.value!!
			w1.addAll(w2)
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
				WAIT_MORE_INFORMATION -> Unit
				ACCEPT_CLOSE_ACCIDENT -> Unit
				DENY_CLOSE_ACCIDENT -> Unit
				EXCALATION -> Unit
				else -> Unit
			}
			return@setOnMenuItemClickListener true
		}

		return@lazy popupMenu
	}

  private fun updateMenuStatus() {
    val role = userViewModel.profile.value!!.role
    val accidentStatus = accidentsViewModel.currentAccident.value!!.status

    val viewStatus = when(role) {
      Role.USER -> {
        when(accidentStatus) {
          AccidentStatus.READY,
          AccidentStatus.WAITING -> View.VISIBLE
          else -> View.INVISIBLE
        }
      }
      Role.ENGINEER -> {
        when(accidentStatus) {
          AccidentStatus.ACTIVE,
          AccidentStatus.IN_WORK -> View.VISIBLE
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
          AccidentStatus.READY -> {
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
            menu.add(0, EXCALATION, 0, R.string.excalation)
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

		val engineer = userViewModel.profile.value!!
		accidentsViewModel.acceptCurrentAccidentToWork(engineer) {
			Toast.makeText(requireContext(), getString(R.string.accident_was_accdepted_to_work), Toast.LENGTH_SHORT).show()
		}
	}

	private fun closeAccident() {
		if (hasLoads) {
			showLoadingErrorMessage()
			return
		}

		accidentsViewModel.closeAccident() {
			Toast.makeText(requireContext(), getString(R.string.accident_was_close), Toast.LENGTH_SHORT).show()
		}
	}

	companion object {
		const val ACCEPT_ACCIDENT_TO_WORK = 1
		const val CLOSE_ACCIDENT = 2
		const val ACCEPT_CLOSE_ACCIDENT = 3
		const val DENY_CLOSE_ACCIDENT = 4
		const val EXCALATION = 5
		const val ADD_INFORMATION = 6
		const val WAIT_MORE_INFORMATION = 7
	}
}