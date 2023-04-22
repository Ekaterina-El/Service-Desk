package com.elka.servicedesk.service.model

data class Division(
  var id: String = "",
  var name: String = "",
  var employers: List<String> = listOf(),
): java.io.Serializable