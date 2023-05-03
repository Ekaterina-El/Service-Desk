package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FieldValue
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

	suspend fun loadAccident(id: String, onSuccess: suspend (Accident) -> Unit): ErrorApp? = try {
		val doc = FirebaseService.accidentsCollection.document(id).get().await()
		val accident = doc.toObject(Accident::class.java)!!
		accident.id = doc.id

		accident.moreInfo = accident.moreInfo.sortedBy { it.date }
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
			accident = accident,
			accidentId = accident.id
		)
		LogsRepository.addLogSync(log)

		onSuccess(accident)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun loadAllAccidentsWithStatus(
		status: AccidentStatus,
		onSuccess: (List<Accident>) -> Unit
	) = try {
		val accidents = FirebaseService.accidentsCollection.whereEqualTo(FIELD_STATUS, status).get().await()
			.mapNotNull { doc ->
				return@mapNotNull try {
					val accident = doc.toObject(Accident::class.java)
					accident.id = doc.id

					accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
					accident.userLocal = UserRepository.loadUser(accident.userId)
					accident.engineerId?.let { accident.engineerLocal = UserRepository.loadUser(it) }

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

	suspend fun loadAccidentsOfDivisions(
		divisionsIds: List<String>, status: AccidentStatus, onSuccess: (List<Accident>) -> Unit
	): ErrorApp? = try {
		val accidents = mutableListOf<Accident>()
		divisionsIds.forEach { divisionId ->
			val divisionAccidents =
				FirebaseService.accidentsCollection.whereEqualTo(FIELD_DIVISION_ID, divisionId)
					.whereEqualTo(FIELD_STATUS, status).get().await().mapNotNull { doc ->
						return@mapNotNull try {
							val accident = doc.toObject(Accident::class.java)
							accident.id = doc.id

							accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
							accident.userLocal = UserRepository.loadUser(accident.userId)
							accident.engineerId?.let { accident.engineerLocal = UserRepository.loadUser(it) }

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
		engineerId: String, onSuccess: (List<Accident>) -> Unit
	): ErrorApp? = try {
		val accidents =
			FirebaseService.accidentsCollection.whereEqualTo(FIELD_ENGINEER_ID, engineerId).get().await()
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

	suspend fun acceptAccidentToWork(
		accident: Accident, engineer: User, division: Division, onSuccess: (Log) -> Unit
	): ErrorApp? = try {
		val accidentId = accident.id
		accident.status = AccidentStatus.IN_WORK
		accident.engineerId = engineer.id
		accident.engineerLocal = engineer

		// change accident status
		changeAccidentField(accidentId, FIELD_STATUS, AccidentStatus.IN_WORK).await()

		// add engineerId to accident
		changeAccidentField(accidentId, FIELD_ENGINEER_ID, engineer.id).await()

		// add log
		val event =
			if (accident.type == AccidentType.INCIDENT) Event.ACCEPT_INCIDENT_TO_WORK else Event.ACCEPT_REQUEST_TO_WORK
		val log = Log(
			editor = engineer,
			division = division,
			accidentId = accidentId,
			accident = accident,
			event = event,
		)
		LogsRepository.addLogSync(log)

		onSuccess(log)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun closeAccidentFromEngineer(
		accident: Accident, engineer: User, division: Division, onSuccess: (Log) -> Unit
	): ErrorApp? = try {
		// change accident status
		changeAccidentField(accident.id, FIELD_STATUS, AccidentStatus.CLOSED).await()

		// add log
		val log = Log(
			editor = engineer,
			division = division,
			accidentId = accident.id,
			accident = accident,
			event = Event.CLOSE_ACCIDENT_BY_ENGINEER,
		)
		LogsRepository.addLogSync(log)

		onSuccess(log)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun closeAccidentFromUser(
		accident: Accident, user: User, division: Division, onSuccess: (Log) -> Unit
	): ErrorApp? = try {
		// change accident status
		changeAccidentField(accident.id, FIELD_STATUS, AccidentStatus.READY).await()

		// add log
		val log = Log(
			editor = user,
			division = division,
			accidentId = accident.id,
			accident = accident,
			event = Event.CLOSE_ACCIDENT_BY_USER,
		)
		LogsRepository.addLogSync(log)

		onSuccess(log)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	private fun changeAccidentField(accidentId: String, field: String, value: Any) =
		FirebaseService.accidentsCollection.document(accidentId).update(field, value)

	suspend fun addAccidentMoreInfo(
		accident: Accident, moreInfo: AccidentMoreInfo, onSuccess: (Log) -> Unit
	): ErrorApp? = try {
		// change accident status
		changeAccidentField(accident.id, FIELD_STATUS, accident.status).await()

		// add moreInfo
		FirebaseService.accidentsCollection.document(accident.id)
			.update(FIELD_MORE_INFO, FieldValue.arrayUnion(moreInfo)).await()

		// add log
		val event =
			if (moreInfo.user!!.role == Role.USER) Event.USER_ADDED_MORE_INFO else Event.ENGINEER_REQUEST_TO_ADD_MORE_INFO
		val log = Log(
			date = moreInfo.date,
			editor = moreInfo.user,
			division = accident.divisionLocal,
			accident = accident,
			accidentId = accident.id,
			event = event
		)
		LogsRepository.addLogSync(log)

		onSuccess(log)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun deleteAllActiveAccidentForDivision(divisionId: String) {
		FirebaseService.accidentsCollection.whereEqualTo(FIELD_DIVISION_ID, divisionId).get().await()
			.forEach { doc ->
				val accident = doc.toObject(Accident::class.java)
				if (accident.status != AccidentStatus.READY) deleteAccident(doc.id)
			}
	}

	private suspend fun deleteAccident(accidentId: String) {
		FirebaseService.accidentsCollection.document(accidentId).delete().await()
	}

	suspend fun sendExcalation(
		accident: Accident,
		reason: String,
		user: User,
		onSuccess: (Log) -> Unit
	): ErrorApp? = try {
		// update status
		changeAccidentField(accident.id, FIELD_STATUS, AccidentStatus.ESCALATION).await()

		// add reason
		changeAccidentField(accident.id, FIELD_REASON_OF_EXCALATION, reason).await()
		changeAccidentField(accident.id, FIELD_SENDER_OF_EXCALATION, user).await()

		// add log
		val log = Log(
			editor = user,
			division = accident.divisionLocal,
			accident = accident,
			accidentId = accident.id,
			event = Event.SENT_EXCALATION
		)
		onSuccess(log)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	private const val FIELD_DIVISION_ID = "divisionId"
	private const val FIELD_ENGINEER_ID = "engineerId"
	private const val FIELD_REASON_OF_EXCALATION = "reasonOfExcalation"
	private const val FIELD_SENDER_OF_EXCALATION = "senderOfExcalation"
	private const val FIELD_STATUS = "status"
	private const val FIELD_MORE_INFO = "moreInfo"
}