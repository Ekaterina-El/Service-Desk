package com.elka.servicedesk.view.ui.engineer

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel
import com.elka.servicedesk.viewModel.EngineerViewModel
import com.elka.servicedesk.viewModel.DivisionsViewModel

abstract class EngineerBaseFragment : UserBaseFragment() {
  protected val divisionsViewModel by activityViewModels<DivisionsViewModel>()
  protected val accidentViewModel by activityViewModels<AccidentsViewModel>()
  protected val engineerViewModel by activityViewModels<EngineerViewModel>()

  protected open val works = listOf(
    Work.LOAD_PROFILE, Work.LOAD_DIVISIONS, Work.LOAD_ACCIDENTS
  )

  protected open val hasLoads: Boolean
    get() {
      val w1 = userViewModel.work.value!!.toMutableList()
      val w2 = divisionsViewModel.work.value!!
      val w3 = accidentViewModel.work.value!!
      w1.addAll(w2)
      w1.addAll(w3)

      return getHasLoads(w1, works)
    }

  fun hideMenu() {
    engineerViewModel.setMenuStatus(false)
  }

  fun showMenu() {
    engineerViewModel.setMenuStatus(true)
  }
}