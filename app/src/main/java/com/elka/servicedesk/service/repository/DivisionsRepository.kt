package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.service.model.Division
import com.google.firebase.FirebaseNetworkException
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
}