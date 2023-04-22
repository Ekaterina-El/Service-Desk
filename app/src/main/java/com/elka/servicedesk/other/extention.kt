package com.elka.servicedesk.other

import java.text.SimpleDateFormat
import java.util.*

val local: Locale by lazy { Locale.getDefault() }
val logSdf: SimpleDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", local) }

fun Date.toLogFormat(): String = logSdf.format(this)
