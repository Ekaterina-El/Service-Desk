package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.Event
import java.util.*

data class Log(
  var id: String = "",
  var date: Date? = null,
  var editor: User? = null,
  var division: Division? = null,
  var event: Event = Event.ADDED_ANALYST,
  var param: String = ""
)

