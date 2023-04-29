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
    val accidents = accidentIds.mapNotNull { loadAccidentSync(it) }
    onSuccess(accidents)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadAccident(id: String, onSuccess: (Accident) -> Unit): ErrorApp? = try {
    val doc = FirebaseService.accidentsCollection.document(id).get().await()
    val accident = doc.toObject(Accident::class.java)!!
    accident.id = doc.id

    accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
    accident.userLocal = UserRepository.loadUser(accident.userId)
    accident.engineerId?.let { accident.engineerLocal = UserRepository.loadUser(it) }

    onSuccess(accident)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  private suspend fun loadAccidentSync(id: String): Accident? {
    var accident: Accident? = null
    loadAccident(id) { loadedAccident ->
      accident = loadedAccident
    }

    return accident
  }

  suspend fun addAccident(
    profile: User, accident: Accident, onSuccess: (Accident) -> Unit
  ): ErrorApp? = try {


    val photosUrl = accident.photosURL.map {
      return@map FirebaseService.loadPhoto(it)
    }
    accident.photosURL = photosUrl

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

  suspend fun loadAccidentsOfEngineer(
    engineerId: String,
    onSuccess: (List<Accident>) -> Unit
  ): ErrorApp? = try {
    val accidents = FirebaseService.accidentsCollection.whereEqualTo(FIELD_ENGINEER_ID, engineerId).get().await()
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
  private const val FIELD_ENGINEER_ID = "engineerId"
}