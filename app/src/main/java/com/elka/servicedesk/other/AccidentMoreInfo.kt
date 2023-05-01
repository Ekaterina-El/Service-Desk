package com.elka.servicedesk.other

import com.elka.servicedesk.service.model.User
import java.util.*

data class AccidentMoreInfo(
	val date: Date = Constants.getCurrentDate(),
	val message: String = "",
	val user: User? = null
) {
	var dateS: String
		get() = date.toLogFormat()
		set(v) {}
}