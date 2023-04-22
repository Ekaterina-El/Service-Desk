package com.elka.servicedesk.view.ui.admin

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AdminsViewModel
import com.elka.servicedesk.viewModel.AnalystsViewModel

abstract class AdminBaseFragment: UserBaseFragment() {
  protected val analystsViewModel by activityViewModels<AnalystsViewModel>()

  protected open val works = listOf(
    Work.LOAD_ANALYSTS, Work.LOAD_PROFILE
  )

  protected open val hasLoads: Boolean
    get() {
      val w1 = userViewModel.work.value!!.toMutableList()
      val w2 = analystsViewModel.work.value!!
      w1.addAll(w2)

      return getHasLoads(w1, works)
    }
}