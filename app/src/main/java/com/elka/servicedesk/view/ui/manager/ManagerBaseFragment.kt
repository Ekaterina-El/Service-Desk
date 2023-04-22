package com.elka.servicedesk.view.ui.manager

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AdminsViewModel

abstract class ManagerBaseFragment: UserBaseFragment() {
  protected val adminViewModel by activityViewModels<AdminsViewModel>()

  protected val works = listOf(
    Work.LOAD_ADMINS, Work.LOAD_PROFILE
  )

  protected val hasLoads: Boolean
    get() {
      val w1 = userViewModel.work.value!!.toMutableList()
      val w2 = adminViewModel.work.value!!
      w1.addAll(w2)

      return when {
        w1.isEmpty() -> false
        else -> w1.map { item -> if (works.contains(item)) 1 else 0 }.reduce { a, b -> a + b } > 0
      }
    }

}