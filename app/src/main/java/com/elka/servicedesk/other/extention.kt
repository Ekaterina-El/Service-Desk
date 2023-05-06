package com.elka.servicedesk.other

import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.repository.DivisionsRepository
import com.elka.servicedesk.service.repository.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val local: Locale by lazy { Locale.getDefault() }
val logSdf: SimpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", local) }
val moreInfoSdf: SimpleDateFormat by lazy { SimpleDateFormat("dd.MM.yyyy HH:mm", local) }
val avgTime: SimpleDateFormat by lazy { SimpleDateFormat("HH:mm:ss", local) }

fun Date.toLogFormat(): String = logSdf.format(this)
fun Date.toMoreInfoFormat(): String = moreInfoSdf.format(this)
fun Date.toAvgTime(): String = avgTime.format(this)

fun Date.timeLeft(): String {
	val totalS = this.time / 1000
	val h = (totalS / 3600).toInt()

	val mS = totalS - h * 3600
	val m = (mS / 60).toInt()

	val s = (mS - m * 60).toInt()

	return "${h.addZero()}:${m.addZero()}:${s.addZero()}"
}

fun Int.addZero() = if (this < 10) "0$this" else "$this"

suspend fun DocumentSnapshot.toAccident(): Accident? = try {
	val accident = this.toObject(Accident::class.java)!!
	accident.id = this.id

	accident.divisionLocal = DivisionsRepository.loadDivision(accident.divisionId)
	accident.userLocal = UserRepository.loadUser(accident.userId)
	accident.engineerId?.let { accident.engineerLocal = UserRepository.loadUser(it) }

	accident
} catch (e: Exception) {
	null
}

fun Long.toDateFormatHMS() = Date(this).timeLeft()

