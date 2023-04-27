package com.elka.servicedesk.view.ui.customer

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel
import com.elka.servicedesk.viewModel.CustomerViewModel
import com.elka.servicedesk.viewModel.DivisionsViewModel

abstract class CustomerBaseFragment : UserBaseFragment() {
  protected val divisionsViewModel by activityViewModels<DivisionsViewModel>()
  protected val accidentViewModel by activityViewModels<AccidentsViewModel>()
  protected val customerViewModel by activityViewModels<CustomerViewModel>()

  protected open val works = listOf(
    Work.LOAD_PROFILE,
    Work.LOAD_DIVISIONS,
    Work.CHANGE_USER_DIVISION,
    Work.UPDATE_USER,
    Work.LOAD_ACCIDENTS,
    Work.ADD_ACCIDENT
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
    customerViewModel.setMenuStatus(false)
  }

  fun showMenu() {
    customerViewModel.setMenuStatus(true)
  }
}