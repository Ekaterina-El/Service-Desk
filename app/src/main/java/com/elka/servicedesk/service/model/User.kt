package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.Role

data class User(
  var id: String = "",
  var firstName: String = "",
  var lastName: String = "",
  var phoneNumber: String = "",
  var email: String = "",

  var divisionId: String = "",
  var divisionLocal: Division? = null,

  var role: Role = Role.USER
) : java.io.Serializable {

  var fullName
    get() = listOf(lastName, firstName).joinToString(" ")
    set(v) {}

}