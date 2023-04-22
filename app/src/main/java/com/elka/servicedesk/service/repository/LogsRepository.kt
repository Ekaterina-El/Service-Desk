package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.service.model.Log
import com.google.firebase.FirebaseNetworkException
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

  suspend fun loadAllLogs(onSuccess: (List<Log>) -> Unit): ErrorApp? = try {
    val logs = FirebaseService.logsCollection.get().await().mapNotNull { doc ->
      return@mapNotNull try {
        val log = doc.toObject(Log::class.java)
        log.id = doc.id
        log
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
}