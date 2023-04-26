package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.*
import java.util.*

data class Accident(
  var id: String = "",
  var subject: String = "",
  var message: String = "",

  var urgency: UrgencyCategory = UrgencyCategory.LOW,
  var category: AccidentCategory = AccidentCategory.NETWORKING_TECHNOLOGIES,
  var photosURL: List<String> = listOf(),

  var divisionId: String = "",
  var divisionLocal: Division? = null,

  var userId: String = "",
  var userLocal: User? = null,

  var createdDate: Date = Constants.getCurrentDate(),
  val status: AccidentStatus = AccidentStatus.ACTIVE,
) {
  var createdDateS: String
    get() = createdDate.toLogFormat()
    set(v) {}

  var info: String
    get() = "${divisionLocal?.name} / ${userLocal?.fullName} / $createdDateS"
    set(v) {}
}

fun List<Accident>.filterBy(s: String) = this.filter {
  it.subject.contains(s, true)
      || it.message.contains(s, true)
      || it.urgency.text.contains(s, true)
      || it.category.text.contains(s, true)
      || it.divisionLocal?.name?.contains(s, true) == true
      || it.userLocal?.fullName?.contains(s, true) == true
      || it.createdDateS.contains(s, true)
      || it.status.text.contains(s, true)
}
