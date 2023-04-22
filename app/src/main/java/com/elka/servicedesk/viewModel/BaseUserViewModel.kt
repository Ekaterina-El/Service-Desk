package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.launch

abstract class BaseUserViewModel(application: Application) : BaseViewModelWithFields(application) {
  abstract val userRole: Role

  private val _users = MutableLiveData<List<User>>(listOf())
  val users get() = _users

  val filter = MutableLiveData("")
  private val _filteredUsers = MutableLiveData<List<User>>(listOf())
  val filteredUsers get() = _filteredUsers

  fun loadUsers() {
    val work = Work.LOAD_USERS
    addWork(work)

    viewModelScope.launch {
      _error.value = UserRepository.loadUsers(userRole) {
        _users.value = it
        filterUsers()
      }
      removeWork(work)
    }
  }


  private fun addNewUser(user: User) {
    val users = _users.value!!.toMutableList()
    users.add(user)
    _users.value = users
    filterUsers()
  }

  private fun removeUser(user: User) {
    val users = _users.value!!.toMutableList()
    users.remove(user)
    _users.value = users
    filterUsers()
  }

  fun filterUsers() {
    val items = _users.value!!
    val filter = filter.value!!

    _filteredUsers.value = when(filter) {
      "" -> items
      else -> items.filterBy(filter)
    }
  }

  fun clearFilterUsers() {
    filter.value = ""
    filterUsers()
  }

  fun clear() {
    clearDialog()
    _externalAction.value = null
    _error.value = null
    _fieldErrors.value = listOf()
    filter.value = ""
    _users.value = listOf()
    _filteredUsers.value = listOf()
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

  var addedUser = MutableLiveData<User?>(null)
  var password: String? = null

  private var currentUserEmail: String? = null
  private var currentUserPassword: String? = null
  fun setCurrentUserCredentials(email: String, password: String) {
    currentUserEmail = email
    currentUserPassword = password
  }

  fun tryRegistration(editorProfile: User, divisions: List<Division> = listOf()) {
    if (!checkFields()) return

    val work = Work.REGISTRATION_USER
    addWork(work)

    viewModelScope.launch {
      val user = newUser
      user.divisionsLocal = divisions
      user.divisionsId = divisions.map { it.id }

      password = Generator.genPassword()

      _error.value = UserRepository.registrationUser(user.email, password!!) { uid ->
        user.id = uid
        _error.value = UserRepository.addUser(user, editorProfile) {
          addNewUser(user)
          addedUser.value = user
        }
      }

      UserRepository.logout {
        _error.value = UserRepository.login(currentUserEmail!!, currentUserPassword!!) {}
      }
      if (_error.value == null) _externalAction.value = Action.GO_NEXT
      removeWork(work)
    }
  }

  fun afterNotifyAddedUser() {
    password = null
    addedUser.value = null
    clearDialog()
  }

  open fun clearDialog() {
    firstName.value = ""
    lastName.value = ""
    email.value = ""
  }

  fun blockUser(user: User, deletedBy: User) {
    val work = Work.BLOCK_USER
    addWork(work)


    viewModelScope.launch {
      _error.value = UserRepository.blockUser(user, deletedBy) {
        removeUser(user)
      }
      removeWork(work)
    }
  }

  private val newUser
    get() = User(
      firstName = firstName.value!!,
      lastName = lastName.value!!,
      email = email.value!!,
      role = userRole
    )
}