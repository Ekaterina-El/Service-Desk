package com.elka.servicedesk.viewModel

import android.app.Application
import android.text.method.TextKeyListener.clear
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : BaseViewModel(application) {
  private val _profile = MutableLiveData<User?>()
  val profile get() = _profile

  fun loadCurrentUserProfile() {
    val work = Work.LOAD_PROFILE
    addWork(work)

    viewModelScope.launch {
      _error.value = UserRepository.loadCurrentUserProfile { profile ->
        _profile.value = profile
      }
      removeWork(work)
    }
  }

  fun logout() {
    val work = Work.LOGOUT
    addWork(work)

    viewModelScope.launch {
      UserRepository.logout {
        _externalAction.value = Action.RESTART
        clear()
      }
      removeWork(work)
    }
  }

  private fun clear() {
    _profile.value = null
  }
}