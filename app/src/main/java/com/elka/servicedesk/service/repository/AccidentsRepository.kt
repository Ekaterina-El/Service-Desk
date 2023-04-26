package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.service.model.Accident
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await

object AccidentsRepository {
  suspend fun loadAccidents(accidentIds: List<String>, onSuccess: (List<Accident>) -> Unit): ErrorApp? = try {
    val accidents = accidentIds.mapNotNull { loadAccident(it)}
    onSuccess(accidents)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  private suspend fun loadAccident(id: String): Accident? {
    return try {
      val doc = FirebaseService.accidentsCollection.document(id).get().await()
      val accident = doc.toObject(Accident::class.java)!!
      accident.id = doc.id

      accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
      accident.userLocal = UserRepository.loadUser(accident.userId)

      return accident
    } catch (e: java.lang.Exception) { null
    }
  }
}