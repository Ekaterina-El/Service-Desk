package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.service.model.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

object LogsRepository {

	suspend fun addLog(log: Log, onSuccess: (Log) -> Unit): ErrorApp? = try {
		val logNew = addLogSync(log)
		onSuccess(logNew)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun addLogSync(log: Log): Log {
		log.editor?.divisionLocal = null
		val doc = FirebaseService.logsCollection.add(log).await()
		log.id = doc.id
		return log
	}

	suspend fun loadLogs(
		divisionIds: List<String>?, accidentIds: List<String>?, onSuccess: (List<Log>) -> Unit
	): ErrorApp? = try {
		val logs = FirebaseService.logsCollection.get().await().mapNotNull { doc ->
			return@mapNotNull try {
				doc.toLog()
			} catch (e: Exception) {
				null
			}
		}

		val logsOfDivisions =
			if (divisionIds != null) logs.filter { divisionIds.contains(it.division?.id) }.toMutableList()
			else logs.toMutableList()

		val logsOfAccidents =
			if (accidentIds != null) logs.filter { accidentIds.contains(it.accidentId) } else logs.toMutableList()

		val idsOfDivisionsLogs = logsOfDivisions.map { it.id }
		val diffLogsOfAccidents = logsOfAccidents.filter { !idsOfDivisionsLogs.contains(it.id) }
		logsOfDivisions.addAll(diffLogsOfAccidents)

		onSuccess(logsOfDivisions)
		null
	} catch (e: FirebaseNetworkException) {
		Errors.network
	} catch (e: Exception) {
		Errors.unknown
	}

	suspend fun loadAccidentLogs(accidentId: String, onSuccess: (List<Log>) -> Unit): ErrorApp? =
		try {
			val logs =
				FirebaseService.logsCollection.whereEqualTo(FIELD_ACCIDENT_ID, accidentId).get().await()
					.mapNotNull { doc ->
						return@mapNotNull try {
							doc.toLog()
						} catch (e: Exception) {
							null
						}
					}

			onSuccess(logs)
			null
		} catch (e: FirebaseNetworkException) {
			Errors.network
		} catch (e: Exception) {
			Errors.unknown
		}

	private fun DocumentSnapshot.toLog(): Log? {
		val log = this.toObject(Log::class.java)
		log?.id = this.id
		return log
	}

	const val FIELD_ACCIDENT_ID = "accidentId"
}