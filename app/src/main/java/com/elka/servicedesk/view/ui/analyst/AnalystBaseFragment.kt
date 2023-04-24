package com.elka.servicedesk.view.ui.analyst

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel

abstract class AnalystBaseFragment: UserBaseFragment() {
  protected val divisionsViewModel by activityViewModels<DivisionsViewModel>()

  protected open val works = listOf(
    Work.LOAD_PROFILE, Work.LOAD_DIVISIONS
  )

  protected open val hasLoads: Boolean
    get() {
      val w1 = userViewModel.work.value!!.toMutableList()
      val w2 = divisionsViewModel.work.value!!
      w1.addAll(w2)

      return getHasLoads(w1, works)
    }
}