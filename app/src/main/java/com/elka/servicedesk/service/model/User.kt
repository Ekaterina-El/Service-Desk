package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.Role

data class User(
  var id: String = "",
  var firstName: String = "",
  var lastName: String = "",
  var phoneNumber: String = "",
  var email: String = "",

  // for user
  var divisionId: String = "",
  var divisionLocal: Division? = null,
  var accidentsIds: List<String> = listOf(),

  // for analyst
  var divisionsLocal: List<Division> = listOf(),
  var divisionsId: List<String> = listOf(),

  var role: Role = Role.USER
) : java.io.Serializable {


  var fullName
    get() = listOf(lastName, firstName).joinToString(" ")
    set(v) {}
}

fun List<User>.filterBy(search: String) = this.filter {
  it.firstName.contains(search, true) ||
  it.lastName.contains(search, true) ||
  it.fullName.contains(search, true)
}
