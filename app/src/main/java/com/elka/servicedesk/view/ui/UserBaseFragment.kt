package com.elka.servicedesk.view.ui

import androidx.fragment.app.activityViewModels
import com.elka.servicedesk.viewModel.UserViewModel

abstract class UserBaseFragment: BaseFragment() {
  protected val userViewModel by activityViewModels<UserViewModel>()

}