package com.elka.servicedesk.view.ui.manager

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AdminsViewModel

abstract class ManagerBaseFragment: UserBaseFragment() {
  protected val adminViewModel by activityViewModels<AdminsViewModel>()

}