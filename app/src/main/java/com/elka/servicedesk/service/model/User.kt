package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.Role
import com.elka.servicedesk.other.toAvgTime
import com.elka.servicedesk.other.toDateFormatHMS
import com.elka.servicedesk.other.toLogFormat
import java.util.Date

data class User(
  var id: String = "",
  var firstName: String = "",
  var lastName: String = "",
  var email: String = "",

  // for user
  var divisionId: String = "",
  var divisionLocal: Division? = null,
  var accidentsIds: List<String> = listOf(),

  // for engineer
  var divisionsLocal: List<Division> = listOf(),
  var divisionsId: List<String> = listOf(),
  var countOfEnded: Int = 0,
  var avgTimeOfEnding: Long = 0,

  var role: Role = Role.USER
) : java.io.Serializable {


  var fullName
    get() = listOf(lastName, firstName).joinToString(" ")
    set(v) {}

  var avgTimeOfEndingS: String
    get() = avgTimeOfEnding.toDateFormatHMS()
    set(v) {}
}

fun List<User>.filterBy(search: String) = this.filter {
  it.firstName.contains(search, true) ||
  it.lastName.contains(search, true) ||
  it.fullName.contains(search, true)
}
