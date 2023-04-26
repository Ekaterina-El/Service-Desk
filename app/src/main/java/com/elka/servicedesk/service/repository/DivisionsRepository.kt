package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

object DivisionsRepository {
  suspend fun addDivision(
    division: Division, editor: User, onSuccess: (Division) -> Unit
  ): ErrorApp? = try {
    val doc = FirebaseService.divisionsCollection.add(division).await()
    division.id = doc.id

    val log = Log(
      date = Constants.getCurrentDate(),
      editor = editor,
      division = division,
      event = Event.ADDED_DIVISION,
      param = division.name
    )
    LogsRepository.addLogSync(log)

    onSuccess(division)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: java.lang.Exception) {
    Errors.unknown
  }

  suspend fun removeDivision(division: Division, editor: User, onSuccess: () -> Unit): ErrorApp? =
    try {
      FirebaseService.divisionsCollection.document(division.id).delete().await()

      val log = Log(
        date = Constants.getCurrentDate(),
        editor = editor,
        division = division,
        event = Event.REMOVED_DIVISION,
        param = division.name
      )
      LogsRepository.addLogSync(log)

      onSuccess()
      null
    } catch (e: FirebaseNetworkException) {
      Errors.network
    } catch (e: java.lang.Exception) {
      Errors.unknown
    }


  suspend fun getDivisionById(divisionId: String): Division? {
    val doc = FirebaseService.divisionsCollection.document(divisionId).get().await()
    val division = doc.toObject(Division::class.java)
    division?.id = doc.id

    return division
  }

  suspend fun getAllDivisions(onSuccess: (List<Division>) -> Unit): ErrorApp? = try {
    val divisions = FirebaseService.divisionsCollection.get().await().mapNotNull {
      try {
        val doc = it.toObject(Division::class.java)
        doc.id = it.id
        return@mapNotNull doc
      } catch (e: Exception) {
        return@mapNotNull null
      }
    }
    onSuccess(divisions)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: java.lang.Exception) {
    Errors.unknown
  }


  private suspend fun changeList(field: String, divisionId: String, value: Any, action: Action) {
    val fv = when (action) {
      Action.REMOVE -> FieldValue.arrayRemove(value)
      Action.ADD -> FieldValue.arrayUnion(value)
      else -> return
    }

    FirebaseService.divisionsCollection.document(divisionId).update(field, fv).await()
  }

  suspend fun addEmployer(divisionId: String, userId: String, userName: String? = null, division: Division? = null) {
    changeList(FIELD_EMPLOYERS, divisionId, userId, Action.ADD)

    if (userName != null && division != null) {
      val log = Log(
        date = Constants.getCurrentDate(),
        division = division,
        event = Event.ADDED_EMPLOYER,
        param = ": подразделение - ${division.name}; сотрудник - ${userName}"
      )
      LogsRepository.addLogSync(log)
    }
  }

  suspend fun removeEmployer(
    divisionId: String, userId: String, userName: String? = null, division: Division? = null
  ) {
    changeList(FIELD_EMPLOYERS, divisionId, userId, Action.REMOVE)

    if (userName != null && division != null) {
      val log = Log(
        date = Constants.getCurrentDate(),
        division = division,
        event = Event.REMOVED_EMPLOYER,
        param = ": подразделение - ${division.name}; сотрудник - ${userName}"
      )
      LogsRepository.addLogSync(log)
    }
  }

  suspend fun loadDivisions(userId: String, divisionsId: List<String>): List<Division> {
    val divisions = divisionsId.mapNotNull { id ->
      try {
        val doc = FirebaseService.divisionsCollection.document(id).get().await()
        val division = doc.toObject(Division::class.java)!!
        division.id = doc.id
        return@mapNotNull division
      } catch (e: java.lang.NullPointerException) {
        UserRepository.deleteDivisionId(userId, id)
        return@mapNotNull null
      }
    }
    return divisions
  }

  suspend fun loadDivision(divisionId: String): Division? {
    return try {
      val doc = FirebaseService.divisionsCollection.document(divisionId).get().await()
      val division = doc.toObject(Division::class.java)!!
      division.id = doc.id
      division
    } catch (e: java.lang.Exception) {
      null
    }
  }

  private const val FIELD_EMPLOYERS = "employers"
}