package com.elka.servicedesk.other

import com.elka.servicedesk.R


data class FieldError(val field: Field, var errorType: FieldErrorType?)

enum class FieldErrorType(val messageRes: Int) {
  IS_REQUIRE(R.string.is_require), IS_NOT_EMAIL(R.string.is_no_email), SHORT(R.string.short_error),
}

enum class Field {
  EMAIL, PASSWORD,
}