package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.*
import com.elka.servicedesk.view.list.accidents.AccidentItem
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter
import java.util.*

data class Accident(
  var type: AccidentType = AccidentType.INCIDENT,
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

  var analystId: String? = null,
  var analystLocal: User? = null,

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

fun List<Accident>.splitAndSort(): MutableList<Accident> {
  val items = mutableListOf<Accident>()
  val incidents =
    this.filter { it.type == AccidentType.INCIDENT }.sortedByDescending { it.createdDate }
  val requests =
    this.filter { it.type == AccidentType.REQUEST }.sortedByDescending { it.createdDate }

  items.addAll(incidents)
  items.addAll(requests)

  return items
}


fun List<Accident>.toAccidentItems() = this.map { AccidentItem(AccidentsAdapter.TYPE_ITEM, it) }