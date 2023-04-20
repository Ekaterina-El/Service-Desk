package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : BaseViewModelWithFields(application) {
  val email = MutableLiveData("")
  val password = MutableLiveData("")


  override val fields: HashMap<Field, MutableLiveData<Any?>>
    get() = hashMapOf(
      Pair(Field.EMAIL, email as MutableLiveData<Any?>),
      Pair(Field.PASSWORD, password as MutableLiveData<Any?>),
    )

  fun clear() {
    email.value = ""
    password.value = ""
    _externalAction.value = null
    _error.value = null
    _fieldErrors.value = listOf()
    clearWork()
  }

  fun tryAuth() {
    if (!checkFields()) return

    val work = Work.AUTH_USER
    addWork(work)

    viewModelScope.launch {
      _error.value = UserRepository.login(email.value!!, password.value!!) {
        _externalAction.value = Action.LOAD_PROFILE
      }
      removeWork(work)
    }

  }
}

