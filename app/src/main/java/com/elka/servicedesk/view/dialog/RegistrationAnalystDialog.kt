package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.elka.servicedesk.databinding.RegistrationAnalystBinding
import com.elka.servicedesk.other.Credentials
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.other.Selector
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.divisions.DivisionViewHolder
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter
import com.elka.servicedesk.view.list.divisions.DivisionsDinamicAdapter
import com.elka.servicedesk.viewModel.AnalystsViewModel

class RegistrationAnalystDialog(
  context: Context,
  val owner: LifecycleOwner,
  val viewModel: AnalystsViewModel,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: RegistrationAnalystBinding
  private lateinit var profile: User

  private val divisionsAdapterListener by lazy {
    object : DivisionViewHolder.Companion.Listener {
      override fun onDelete(division: Division) {
        divisionsAdapter.removeItem(division)
        viewModel.removeDivision(division)
      }
    }
  }

  private val divisionsAdapter: DivisionsDinamicAdapter by lazy {
    DivisionsDinamicAdapter(divisionsAdapterListener)
  }

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

  private val addedUserObserver = Observer<User?> { user ->
    val password = viewModel.password

    if (user == null || password == null) return@Observer
    viewModel.afterNotifyAddedUser()
    listener.afterAdded(user, password)
  }

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = RegistrationAnalystBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@RegistrationAnalystDialog
      lifecycleOwner = this@RegistrationAnalystDialog.owner
      viewModel = this@RegistrationAnalystDialog.viewModel
      divisionsAdapter = this@RegistrationAnalystDialog.divisionsAdapter
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    setOnDismissListener { disagree() }
  }


  fun open(cuCredentials: Credentials, profile: User, divisions: List<Division>) {
    this@RegistrationAnalystDialog.profile = profile

    viewModel.setCurrentUserCredentials(cuCredentials.email, cuCredentials.password)
    viewModel.fieldErrors.observe(owner, fieldErrorsObserver)
    viewModel.addedUser.observe(owner, addedUserObserver)

    val decorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    binding.divisionsList.addItemDecoration(decorator)

    val spinnerAdapter = DivisionsAdapter(context, divisions)
    binding.divisionSpinner.adapter = spinnerAdapter

    show()
  }

  fun disagree() {
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.addedUser.removeObserver(addedUserObserver)

    divisionsAdapter.clear()

    viewModel.clearDialog()
    dismiss()
  }

  fun agree() {
    val divisions = viewModel.divisions.value!!
    if (divisions.isEmpty()) return
    viewModel.tryRegistration(profile, divisions)
  }


  fun addDivision() {
    val division = binding.divisionSpinner.selectedItem as Division
    if (divisionsAdapter.getAllItems().contains(division)) return
    divisionsAdapter.addItem(division)
    viewModel.addDivision(division)
  }

  companion object {
    interface Listener {
      fun afterAdded(user: User, password: String)
    }
  }
}