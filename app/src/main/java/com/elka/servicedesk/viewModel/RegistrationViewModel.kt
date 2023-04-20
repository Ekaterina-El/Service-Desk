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

class RegistrationViewModel(application: Application) : BaseViewModelWithFields(application) {
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

  override val fields: HashMap<Field, MutableLiveData<Any?>>
    get() = hashMapOf(
      Pair(Field.FIRST_NAME, firstName as MutableLiveData<Any?>),
      Pair(Field.LAST_NAME, lastName as MutableLiveData<Any?>),
      Pair(Field.PHONE_NUMBER, phoneNumber as MutableLiveData<Any?>),
      Pair(Field.EMAIL, email as MutableLiveData<Any?>),
      Pair(Field.DIVISION, division as MutableLiveData<Any?>),
      Pair(Field.PASSWORD, password as MutableLiveData<Any?>),
    )

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
    if (!checkFields()) return

    val work = Work.REGISTRATION_USER
    addWork(work)

    viewModelScope.launch {
      val user = newUser
      _error.value = UserRepository.registrationUser(user.email, password.value!!) { uid ->
        user.id = uid
        _error.value = UserRepository.addUser(user) {
        }
      }

      UserRepository.logout {}
      if (_error.value == null) _externalAction.value = Action.GO_NEXT
      removeWork(work)
    }

  }

  private val newUser get() = User(
    firstName = firstName.value!!,
    lastName = lastName.value!!,
    phoneNumber = phoneNumber.value!!,
    email = email.value!!,
    divisionId = division.value!!.id,
    role = Role.USER
  )
}

