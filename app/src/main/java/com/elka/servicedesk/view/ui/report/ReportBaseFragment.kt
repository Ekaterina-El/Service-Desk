package com.elka.servicedesk.view.ui.report

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.ReportViewModel

abstract class ReportBaseFragment : UserBaseFragment() {
	protected val reportViewModel by activityViewModels<ReportViewModel>()

	protected open val works = listOf(
		Work.LOAD_ACCIDENTS
	)

	protected open val hasLoads: Boolean
		get() {
			val w1 = userViewModel.work.value!!.toMutableList()
			val w2 = reportViewModel.work.value!!
			w1.addAll(w2)

			return getHasLoads(w1, works)
		}

	protected val onBackPressedCallback by lazy {
		object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				navController.popBackStack()
			}
		}
	}

	fun hideMenu() {
		reportViewModel.setMenuStatus(false)
	}

	fun showMenu() {
		reportViewModel.setMenuStatus(true)
	}
}