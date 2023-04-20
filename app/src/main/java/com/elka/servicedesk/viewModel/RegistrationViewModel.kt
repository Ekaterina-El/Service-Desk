package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Division
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : BaseViewModel(application) {
  val firstName = MutableLiveData("")
  val lastName = MutableLiveData("")
  val phoneNumber = MutableLiveData("")
  val email = MutableLiveData("")
  val password = MutableLiveData("")

  private val _division = MutableLiveData<Division?>(null)
  val division get() = _division

  fun setDivision(division: Division) {
    _division.value = division
  }

  private val _fieldErrors = MutableLiveData<List<FieldError>>(listOf())
  val fieldErrors get() = _fieldErrors

  fun clear() {
    firstName.value = ""
    lastName.value = ""
    phoneNumber.value = ""
    email.value = ""
    password.value = ""
    _division.value = null
    _externalAction.value = null
    _error.value = null
    _fieldErrors.value = listOf()
    clearWork()
  }

  fun tryRegistration() {
    val work = Work.REGISTRATION_USER
    addWork(work)

    viewModelScope.launch {
//    _error.value = ...
      delay(1000)
      removeWork(work)
    }
  }
}

