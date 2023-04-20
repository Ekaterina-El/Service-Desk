package com.elka.servicedesk.other

object Validator {
    fun isEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun checkPasswordField(password: String) = when {
        password.isEmpty() -> FieldErrorType.IS_REQUIRE
        password.length < 8 -> FieldErrorType.SHORT
        else -> null
    }

    fun checkEmailField(email: String) = when {
        email.isEmpty() -> FieldErrorType.IS_REQUIRE
        !isEmail(email) -> FieldErrorType.IS_NOT_EMAIL
        else -> null
    }
}