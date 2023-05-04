package com.elka.servicedesk.other

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val local: Locale by lazy { Locale.getDefault() }
val logSdf: SimpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", local) }
val moreInfoSdf: SimpleDateFormat by lazy { SimpleDateFormat("dd.MM.yyyy HH:mm", local) }

fun Date.toLogFormat(): String = logSdf.format(this)
fun Date.toMoreInfoFormat(): String = moreInfoSdf.format(this)

fun Date.timeLeft(): String {
	val totalS = this.time / 1000
	val h = (totalS / 3600).toInt()

	val mS = totalS - h * 3600
	val m = (mS / 60).toInt()

	val s = (mS - m * 60).toInt()

	return "${h.addZero()}:${m.addZero()}:${s.addZero()}"
}

fun Int.addZero() = if (this < 10) "0$this" else "$this"
