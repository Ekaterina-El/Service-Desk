package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await

object AccidentsRepository {
  suspend fun loadAccidents(
    accidentIds: List<String>, onSuccess: (List<Accident>) -> Unit
  ): ErrorApp? = try {
    val accidents = accidentIds.mapNotNull { loadAccident(it) }
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
      accident.analystId?.let { accident.analystLocal = UserRepository.loadUser(it) }

      return accident
    } catch (e: java.lang.Exception) {
      null
    }
  }

  suspend fun addAccident(
    profile: User, accident: Accident, onSuccess: (Accident) -> Unit
  ): ErrorApp? = try {
    // TODO: load images
    // accident.photoURL = ...

    val user = accident.userLocal
    val division = accident.divisionLocal
    accident.userLocal = null
    accident.divisionLocal = null

    accident.id = FirebaseService.accidentsCollection.add(accident).await().id
    accident.userLocal = user
    accident.divisionLocal = division

    // add to profile
    UserRepository.addAccidentId(user!!.id, accident.id)

    // add id to division
    DivisionsRepository.addAccidentId(division!!.id, accident.id)


    val event =
      if (accident.type == AccidentType.INCIDENT) Event.ADDED_INCIDENT else Event.ADDED_REQUEST
    val params = ": ${accident.category.text} / ${accident.subject}"

    // add log
    val log = Log(
      date = Constants.getCurrentDate(),
      editor = user,
      division = division,
      event = event,
      param = params,
    )
    LogsRepository.addLogSync(log)

    onSuccess(accident)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadAccidentsOfDivisions(
    divisionsIds: List<String>, onSuccess: (List<Accident>) -> Unit
  ): ErrorApp? = try {
    val accidents = mutableListOf<Accident>()
    divisionsIds.forEach { divisionId ->
      val divisionAccidents =
        FirebaseService.accidentsCollection.whereEqualTo(FIELD_DIVISION_ID, divisionId).get()
          .await().mapNotNull { doc ->
            return@mapNotNull try {
              val accident = doc.toObject(Accident::class.java)
              accident.id = doc.id

              accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
              accident.userLocal = UserRepository.loadUser(accident.userId)

              accident
            } catch (e: Exception) {
              null
            }
          }
      accidents.addAll(divisionAccidents)
    }

    onSuccess(accidents)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadAccidentsOfAnalyst(
    analystId: String,
    onSuccess: (List<Accident>) -> Unit
  ): ErrorApp? = try {
    val accidents = FirebaseService.accidentsCollection.whereEqualTo(FIELD_ANALTYST_ID, analystId).get().await()
      .mapNotNull { doc ->
        return@mapNotNull try {
          val accident = doc.toObject(Accident::class.java)
          accident.id = doc.id

          accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
          accident.userLocal = UserRepository.loadUser(accident.userId)

          accident
        } catch (e: Exception) {
          null
        }
      }
    onSuccess(accidents)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  private const val FIELD_DIVISION_ID = "divisionId"
  private const val FIELD_ANALTYST_ID = "analystId"
}