package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.elka.servicedesk.databinding.RegistrationAdminBinding
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.viewModel.AdminsViewModel

class RegistrationAdminDialog(
  context: Context,
  val owner: LifecycleOwner,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: RegistrationAdminBinding
  private lateinit var viewModel: AdminsViewModel
  private lateinit var profile: User

  private val fieldErrorsObserver = Observer<List<FieldError>> { fields ->
    binding.layoutEmail.error = ""
    binding.layoutFirstName.error = ""
    binding.layoutLastName.error = ""

    if (fields.isEmpty()) return@Observer

    for (field in fields) {
      val view = when (field.field) {
        Field.EMAIL -> binding.layoutEmail
        Field.FIRST_NAME -> binding.layoutFirstName
        Field.LAST_NAME -> binding.layoutLastName
        else -> continue
      }

      view.error = context.getString(field.errorType!!.messageRes)
    }
  }

  private val addedEditorObserver = Observer<User?> { user ->
    val password = viewModel.password

    if (user == null || password == null) return@Observer
    viewModel.afterNotifyAddedUser()
    listener.afterAdded(user, password)

  }

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = RegistrationAdminBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@RegistrationAdminDialog
      lifecycleOwner = this@RegistrationAdminDialog.owner
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    setOnDismissListener { disagree() }
  }


  fun open(viewModel: AdminsViewModel, currentUserEmail: String, currentUserPassword: String, profile: User) {
    this@RegistrationAdminDialog.viewModel = viewModel
    this@RegistrationAdminDialog.profile = profile
    binding.viewModel = viewModel

    viewModel.setCurrentUserCredentials(currentUserEmail, currentUserPassword)
    viewModel.fieldErrors.observe(owner, fieldErrorsObserver)
    viewModel.addedUser.observe(owner, addedEditorObserver)
    show()
  }

  fun disagree() {
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.addedUser.removeObserver(addedEditorObserver)
    viewModel.clearDialog()
    dismiss()
  }

  fun agree() {
    viewModel.tryRegistration(profile)
  }

  companion object {
    interface Listener {
      fun afterAdded(user: User, password: String)
    }
  }
}