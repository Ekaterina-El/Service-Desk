package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.Event
import com.elka.servicedesk.other.toLogFormat
import java.util.*

data class Log(
	var id: String = "",
	var date: Date? = null,
	var editor: User? = null,
	var division: Division? = null,
	var event: Event = Event.ADDED_ENGINEER,
	var param: String = ""
) : java.io.Serializable {
  var dateS
    get() = date?.toLogFormat() ?: ""
    set(v) {}

  var text: String
    get() {
      var str = "[${dateS}] ${event.text} $param "
      if (division != null && editor != null) str += "(Подразделение: ${division!!.name}; Редактирующий: ${editor!!.fullName} - ${editor!!.role.text})"
      else if (division != null && editor == null) str += "(Подразделение: ${division!!.name})"
      else if (division === null && editor != null) str += "(Редактирующий: ${editor!!.fullName} - ${editor!!.role.text})"

      return str
    }
    set(v) {}
}

fun List<Log>.filterBy(search: String) = this.filter {
  it.editor?.fullName?.contains(search, true) == true || it.division?.name?.contains(
    search, true
  ) == true || it.param.contains(search, true) || it.event.text.contains(
    search,
    true
  ) || it.dateS.contains(search, true) || it.editor?.role?.text?.contains(search, true) == true
}