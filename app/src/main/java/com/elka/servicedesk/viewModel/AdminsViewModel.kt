package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.launch

class AdminsViewModel(application: Application) : BaseViewModelWithFields(application) {
  private val _admins = MutableLiveData<List<User>>(listOf())
  val admins get() = _admins

  fun loadAdmins() {
    val work = Work.LOAD_ADMINS
    addWork(work)

    viewModelScope.launch {
      _error.value = UserRepository.loadAdmins {
        _admins.value = it
      }
      removeWork(work)
    }
  }

  private fun addNewAdmin(user: User) {
    val admins = _admins.value!!.toMutableList()
    admins.add(user)
    _admins.value = admins
  }

  fun clear() {
    clearDialog()
    _externalAction.value = null
    _error.value = null
    _fieldErrors.value = listOf()
    clearWork()
  }

  val firstName = MutableLiveData("")
  val lastName = MutableLiveData("")
  val email = MutableLiveData("")


  override val fields: HashMap<Field, MutableLiveData<Any?>>
    get() = hashMapOf(
      Pair(Field.FIRST_NAME, firstName as MutableLiveData<Any?>),
      Pair(Field.LAST_NAME, lastName as MutableLiveData<Any?>),
      Pair(Field.EMAIL, email as MutableLiveData<Any?>),
    )

  var addedAdmin = MutableLiveData<User?>(null)
  var password: String? = null

  private var currentUserEmail: String? = null
  private var currentUserPassword: String? = null
  fun setCurrentUserCredentials(email: String, password: String) {
    currentUserEmail = email
    currentUserPassword = password
  }


  fun tryRegistration() {
    if (!checkFields()) return

    val work = Work.REGISTRATION_USER
    addWork(work)

    viewModelScope.launch {
      val user = newUser
      password = Generator.genPassword()

      _error.value = UserRepository.registrationUser(user.email, password!!) { uid ->
        user.id = uid
        _error.value = UserRepository.addUser(user) {
          addNewAdmin(user)
          addedAdmin.value = user
        }
      }

      UserRepository.logout {
        _error.value = UserRepository.login(currentUserEmail!!, currentUserPassword!!) {}
      }
      if (_error.value == null) _externalAction.value = Action.GO_NEXT
      removeWork(work)
    }
  }

  fun afterNotifyAddedAdmin() {
    password = null
    addedAdmin.value = null
    clearDialog()
  }

  private fun clearDialog() {
    firstName.value = ""
    lastName.value = ""
    email.value = ""
  }

  private val newUser
    get() = User(
      firstName = firstName.value!!,
      lastName = lastName.value!!,
      email = email.value!!,
      role = Role.ADMIN
    )
}

