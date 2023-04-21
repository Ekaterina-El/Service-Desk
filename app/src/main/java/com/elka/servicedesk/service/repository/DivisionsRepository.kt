package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.service.model.Division
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

object DivisionsRepository {
  suspend fun addDivision(division: Division, onSuccess: (Division) -> Unit): ErrorApp? = try {
    val doc = FirebaseService.divisionsCollection.add(division).await()
    division.id = doc.id
    onSuccess(division)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: java.lang.Exception) {
    Errors.unknown
  }

  suspend fun getDivisionById(divisionId: String): Division? {
    val doc = FirebaseService.divisionsCollection.document(divisionId).get().await()
    val division = doc.toObject(Division::class.java)!!
    division.id = doc.id

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

  suspend fun addEmployer(divisionId: String, userId: String) {
    changeList(FIELD_EMPLOYERS, divisionId, userId, Action.ADD)
  }

  suspend fun removeEmployer(divisionId: String, userId: String) {
    changeList(FIELD_EMPLOYERS, divisionId, userId, Action.REMOVE)
  }

  private const val FIELD_EMPLOYERS = "employers"
}