package com.elka.servicedesk.view.ui.admin

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel
import com.elka.servicedesk.viewModel.AdminViewModel
import com.elka.servicedesk.viewModel.EngineersViewModel
import com.elka.servicedesk.viewModel.DivisionsViewModel

abstract class AdminBaseFragment: UserBaseFragment() {
  protected val engineersViewModel by activityViewModels<EngineersViewModel>()
  protected val divisionsViewModel by activityViewModels<DivisionsViewModel>()
  protected val accidentViewModel by activityViewModels<AccidentsViewModel>()
  protected val adminViewModel by activityViewModels<AdminViewModel>()


  protected open val works = listOf(
    Work.LOAD_USERS, Work.LOAD_PROFILE,
    Work.UPDATE_USER, Work.LOAD_DIVISIONS,
    Work.BLOCK_USER, Work.LOAD_LOGS,
    Work.ADD_DIVISION, Work.REMOVE_DIVISION,
    Work.LOAD_ACCIDENT
  )

  protected open val hasLoads: Boolean
    get() {
      val w1 = userViewModel.work.value!!.toMutableList()
      val w2 = engineersViewModel.work.value!!
      val w3 = divisionsViewModel.work.value!!
      val w4 = accidentViewModel.work.value!!
      w1.addAll(w4)
      w1.addAll(w3)
      w1.addAll(w2)

      return getHasLoads(w1, works)
    }

  fun hideMenu() {
    adminViewModel.setMenuStatus(false)
  }

  fun showMenu() {
    adminViewModel.setMenuStatus(true)
  }
}