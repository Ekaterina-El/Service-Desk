package com.elka.servicedesk.viewModel

import android.app.Application
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.service.repository.UserRepository

class SplashViewModel(application: Application) : BaseViewModel(application) {
  fun checkLoginStatus() {
    val uid = UserRepository.currentUid
    _externalAction.value = if (uid == null) Action.GO_LOGIN else Action.GO_PROFILE
  }
}