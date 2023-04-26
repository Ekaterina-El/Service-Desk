package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AddAccidentDialogBinding
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.viewModel.AccidentsViewModel

class AddAccidentDialog(
  context: Context,
  private val owner: LifecycleOwner,
  val viewModel: AccidentsViewModel,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: AddAccidentDialogBinding
  private lateinit var profile: User
  private lateinit var accidentType: AccidentType

  val workObserver = Observer<List<Work>> {
    setCancelable(it.isEmpty())
  }


  private val fieldErrorsObserver = Observer<List<FieldError>> { fields ->
    binding.layoutSubject.error = ""
    binding.layoutMessage.error = ""

    if (fields.isEmpty()) return@Observer

    for (field in fields) {
      val view = when (field.field) {
        Field.SUBJECT -> binding.layoutSubject
        Field.MESSAGE -> binding.layoutMessage
        else -> continue
      }

      view.error = context.getString(field.errorType!!.messageRes)
    }
  }

  private val addedAccidentObserver = Observer<Accident?> { accident ->
    if (accident == null) return@Observer
    listener.afterAdded(accident)
  }

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = AddAccidentDialogBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@AddAccidentDialog
      lifecycleOwner = this@AddAccidentDialog.owner
      viewModel = this@AddAccidentDialog.viewModel
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setOnDismissListener { disagree() }
  }

  private val requestCategoriesItems by lazy { getRequestCategoriesSpinnerItems() }
  private val incidentCategoriesItems by lazy { getIncidentCategoriesSpinnerItems() }

  private val urgenciesItems by lazy { getUrgencyCategoriesSpinnerItems() }
  private val urgenciesAdapter by lazy {
    SpinnerAdapter(context, urgenciesItems)
  }

  fun open(profile: User, accidentType: AccidentType) {
    binding.title.text =
      if (accidentType == AccidentType.REQUEST) context.getString(R.string.adding_request) else context.getString(
        R.string.about_incident
      )

    this@AddAccidentDialog.profile = profile
    this@AddAccidentDialog.accidentType = accidentType

    viewModel.fieldErrors.observe(owner, fieldErrorsObserver)
    viewModel.addedAccident.observe(owner, addedAccidentObserver)
    viewModel.work.observe(owner, workObserver)

    binding.categorySpinner.onItemSelectedListener = categoriesListener
    binding.urgencySpinner.onItemSelectedListener = urgencyListener

    binding.categorySpinner.adapter =
      if (accidentType == AccidentType.REQUEST) SpinnerAdapter(context, requestCategoriesItems)
      else SpinnerAdapter(context, incidentCategoriesItems)

    binding.urgencySpinner.adapter = urgenciesAdapter
    show()
  }

  fun disagree() {
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.addedAccident.removeObserver(addedAccidentObserver)
    viewModel.work.removeObserver(workObserver)

    viewModel.clearDialog()
    dismiss()
  }

  fun agree() {
    val accident = viewModel.getNewAccident(profile, accidentType)
    viewModel.tryAddAccident(profile, accident)
  }

  private val categoriesListener by lazy {
    Selector {
      val spinner = it as SpinnerItem
      val accidentCategory = spinner.value as AccidentCategory
      viewModel.setAccidentCategory(accidentCategory)
    }
  }

  private val urgencyListener by lazy {
    Selector {
      val spinner = it as SpinnerItem
      val urgencyCategory = spinner.value as UrgencyCategory
      viewModel.setUrgencyCategory(urgencyCategory)
    }
  }

  companion object {
    interface Listener {
      fun afterAdded(accident: Accident)
    }
  }
}