package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AddDivisionDialogBinding
import com.elka.servicedesk.databinding.RegistrationAdminBinding
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.viewModel.AdminsViewModel

class AddDivisionDialog(
  context: Context,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: AddDivisionDialogBinding
  private var divisions = listOf<Division>()

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = AddDivisionDialogBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@AddDivisionDialog
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    setOnDismissListener { disagree() }
  }


  fun open(divisions: List<Division>) {
    binding.layoutDivisionName.error = ""
    this.divisions = divisions
    show()
  }

  fun disagree() {
    dismiss()
  }

  fun agree() {
    val divisionName = binding.divisionName.text.toString()
    val equalName = divisions.firstOrNull { it.name == divisionName }
    if (equalName != null) {
      binding.layoutDivisionName.error = context.getString(R.string.no_uniq_division)
      return
    }

    binding.layoutDivisionName.error = ""
    listener.onSave(Division(name = divisionName))
  }

  companion object {
    interface Listener {
      fun onSave(division: Division)
    }
  }
}