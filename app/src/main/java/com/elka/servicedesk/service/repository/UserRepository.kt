package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UserRepository {
  private val auth = Firebase.auth
  val currentUid get() = auth.currentUser?.uid

  suspend fun registrationUser(
    email: String, password: String, onSuccess: suspend (uid: String) -> Unit
  ): ErrorApp? = try {
    val uid = auth.createUserWithEmailAndPassword(email, password).await().user!!.uid
    onSuccess(uid)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: FirebaseAuthWeakPasswordException) {
    Errors.weakPassword
  } catch (e: FirebaseAuthUserCollisionException) {
    Errors.userCollision
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun addUser(
    user: User,
    editor: User? = null,
    division: Division? = null,
    onSuccess: suspend ((Log?) -> Unit) = {}
  ): ErrorApp? = try {
    user.divisionsLocal = listOf()
    user.divisionLocal = null

    FirebaseService.usersCollection.document(user.id).set(user).await()
    if (user.role == Role.USER) DivisionsRepository.addEmployer(user.divisionId, user.id)


    val event = when (user.role) {
      Role.USER -> Event.REGISTERED_USER
      Role.ANALYST -> Event.ADDED_ANALYST
      Role.ADMIN -> Event.ADDED_ADMIN
      Role.MANAGER -> null
    }

    if (event != null) {
      val log = Log(
        date = Constants.getCurrentDate(),
        editor = editor,
        division = division,
        event = event,
        param = user.fullName
      )
      val logNew = LogsRepository.addLogSync(log)
      onSuccess(logNew)
    } else {
      onSuccess(null)
    }

    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun logout(onSuccess: suspend () -> Unit) {
    auth.signOut()
    onSuccess()
  }

  suspend fun login(email: String, password: String, onSuccess: () -> Unit): ErrorApp? = try {
    auth.signInWithEmailAndPassword(email, password).await()
    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: FirebaseAuthInvalidUserException) {
    Errors.invalidEmailPassword
  } catch (e: FirebaseAuthInvalidCredentialsException) {
    Errors.invalidEmailPassword
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadCurrentUserProfile(onSuccess: (User) -> Unit): ErrorApp? = try {
    val profile = getUserById(currentUid!!)
    onSuccess(profile)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: java.lang.NullPointerException) {
    Errors.userWasBlocked
  } catch (e: Exception) {
    Errors.unknown
  }

  private suspend fun getUserById(id: String): User {
    val doc = FirebaseService.usersCollection.document(id).get().await()
    val user = doc.toObject(User::class.java)
    user!!.id = doc.id

    if (user.role == Role.USER && user.divisionId != "") {
      val division = DivisionsRepository.getDivisionById(user.divisionId)
      if (division == null) setDivisionId(user.id, "")
      else user.divisionLocal = division
    }
    if (user.role == Role.ANALYST) user.divisionsLocal =
      DivisionsRepository.loadDivisions(user.id, user.divisionsId)
    return user
  }

  private fun setDivisionId(userId: String, divisionId: String) {
    FirebaseService.usersCollection.document(userId).update(FIELD_DIVISION_ID, divisionId)
  }

  suspend fun loadUsers(userRole: Role, onSuccess: (List<User>) -> Unit): ErrorApp? = try {
    val users = FirebaseService.usersCollection.whereEqualTo(FIELD_ROLE, userRole).get().await()
      .mapNotNull { doc ->
        return@mapNotNull try {
          val user = doc.toObject(User::class.java)
          user.id = doc.id

          if (userRole == Role.ANALYST) {
            user.divisionsLocal = DivisionsRepository.loadDivisions(user.id, user.divisionsId)
          }
          user
        } catch (e: Exception) {
          null
        }
      }
    onSuccess(users)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun blockUser(user: User, deletedBy: User, onSuccess: () -> Unit): ErrorApp? = try {
    val event = when (user.role) {
      Role.ADMIN -> Event.BLOCKED_ADMIN
      Role.ANALYST -> Event.BLOCKED_ANALYST
      else -> null
    }

    if (event != null) {
      // delete user doc
      FirebaseService.usersCollection.document(user.id).delete().await()

      // add log about deleted
      val log = Log(
        date = Constants.getCurrentDate(),
        editor = deletedBy,
        division = user.divisionLocal,
        event = event,
        param = user.fullName
      )
      LogsRepository.addLogSync(log)
    }
    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun updateUser(user: User, editedBy: User, onSuccess: () -> Unit): ErrorApp? = try {
    FirebaseService.usersCollection.document(user.id).set(user).await()

    val event = when (user.role) {
      Role.USER -> Event.UPDATE_USER
      Role.ANALYST -> Event.UPDATE_ANALYST
      Role.ADMIN -> Event.UPDATE_ADMIN
      Role.MANAGER -> Event.UPDATE_MANAGER
    }

    val log = Log(
      date = Constants.getCurrentDate(),
      editor = editedBy,
      division = user.divisionLocal,
      event = event,
      param = user.fullName
    )
    LogsRepository.addLogSync(log)

    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun addDivisionsId(userId: String, divisionId: String) {
    changeList(FIELD_DIVISIONS_ID, userId, divisionId, Action.ADD)
  }

  suspend fun deleteDivisionId(userId: String, divisionId: String) {
    changeList(FIELD_DIVISIONS_ID, userId, divisionId, Action.REMOVE)
  }

  private suspend fun changeList(field: String, userId: String, value: Any, action: Action) {
    val fv = when (action) {
      Action.REMOVE -> FieldValue.arrayRemove(value)
      Action.ADD -> FieldValue.arrayUnion(value)
      else -> return
    }

    FirebaseService.usersCollection.document(userId).update(field, fv).await()
  }

  suspend fun changeDivision(user: User, newDivision: Division, onSuccess: () -> Unit): ErrorApp? =
    try {
      val uid = user.id
      val oldDivision = user.divisionLocal

      // user note: update divisionId
      setDivisionId(uid, newDivision.id)

      // oldDivision (if exists) note: delete userId list on employees
      if (oldDivision != null) DivisionsRepository.removeEmployer(oldDivision.id, uid, user.fullName, oldDivision)

      // newDivision note: add userId list on employees
      DivisionsRepository.addEmployer(newDivision.id, uid, user.fullName, newDivision)

      val params =
        if (oldDivision == null) ": установлено подразделение \"${newDivision.name}\"" else
          ": подразделение \"${oldDivision.name}\" заменено на подразделение \"${newDivision.name}\"; сотурдник - ${user.fullName}"
      val log = Log(
        date = Constants.getCurrentDate(),
        event = Event.CHANGED_DIVISION,
        param = params
      )
      LogsRepository.addLogSync(log)

      onSuccess()
      null
    } catch (e: FirebaseNetworkException) {
      Errors.network
    } catch (e: Exception) {
      Errors.unknown
    }

  const val FIELD_ROLE = "role"
  const val FIELD_DIVISIONS_ID = "divisionsId"
  const val FIELD_DIVISION_ID = "divisionId"
}