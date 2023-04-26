package com.elka.servicedesk.service.model

data class Division(
  var id: String = "",
  var name: String = "",
  var employers: List<String> = listOf(),
  var accidentsIds: List<String> = listOf()
) : java.io.Serializable

fun List<Division>.filterBy(search: String) = this.filter {
  it.name.contains(search, true)
}
