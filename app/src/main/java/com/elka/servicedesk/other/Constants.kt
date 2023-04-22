package com.elka.servicedesk.other

import java.util.*


object Constants {
  private val calendar: Calendar = Calendar.getInstance()
  fun getCurrentDate(): Date = calendar.time

  const val SP_NAME = "the_office_club"
  const val SEPARATOR = ":"
  const val CREDENTIALS = "credentials"
  const val LOAD_DELAY = 3000L
  const val RESERVED_TO_ADD = "RESERVED_TO_ADD"
}