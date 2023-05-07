package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.other.FieldErrorType
import com.elka.servicedesk.other.Validator

abstract class BaseViewModelWithFields(application: Application) : BaseViewModel(application) {
  private val requireFields by lazy {
    listOf(
      Field.FIRST_NAME,
      Field.LAST_NAME,
      Field.EMAIL,
      Field.PASSWORD,
      Field.DIVISION,
      Field.MESSAGE,
      Field.SUBJECT,
    )
  }

  private val emailFields by lazy { listOf(Field.EMAIL) }
  private val passwordFields by lazy { listOf(Field.PASSWORD) }

  abstract val fields: HashMap<Field, MutableLiveData<Any?>>

  protected val _fieldErrors = MutableLiveData<List<FieldError>>()
  val fieldErrors get() = _fieldErrors


  open fun checkFields(): Boolean {
    val errors = arrayListOf<FieldError>()

    for (field in fields) {
      val willCheckToEmpty = requireFields.contains(field.key)

      if (willCheckToEmpty) {
        val hasError = when (val value = field.value.value) {
          is String -> value.isEmpty()
          else -> value == null
        }

        if (hasError) {
          errors.add(FieldError(field.key, FieldErrorType.IS_REQUIRE))
          continue
        }
      }

      val willCheckAsEmail = emailFields.contains(field.key)
      if (willCheckAsEmail) {
        val emailError = Validator.checkEmailField(field.value.value!! as String)
        if (emailError != null) {
          errors.add(FieldError(field.key, emailError))
          continue
        }
      }

      val willCheckAsPassword = passwordFields.contains(field.key)
      if (willCheckAsPassword) {
        val passwordError = Validator.checkPasswordField(field.value.value!! as String)
        if (passwordError != null) {
          errors.add(FieldError(field.key, passwordError))
          continue
        }
      }
    }

    _fieldErrors.value = errors
    return _fieldErrors.value!!.isEmpty()
  }
}