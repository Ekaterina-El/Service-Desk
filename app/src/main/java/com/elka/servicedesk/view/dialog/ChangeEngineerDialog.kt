package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.elka.servicedesk.databinding.ChangeEngineerBinding
import com.elka.servicedesk.databinding.ChangeUserDivisionBinding
import com.elka.servicedesk.other.SpinnerItem
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter
import com.elka.servicedesk.view.ui.BaseFragment

class ChangeEngineerDialog(
  context: Context,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: ChangeEngineerBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = ChangeEngineerBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@ChangeEngineerDialog
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    setOnDismissListener { disagree() }
  }


  fun open(engineers: List<User>) {
    val items = engineers.map { SpinnerItem(it.fullName, it) }
    val spinnerAdapter = SpinnerAdapter(context, items)
    binding.engineersSpinner.adapter = spinnerAdapter

    show()
  }
  fun disagree() {
    dismiss()
  }

  fun agree() {
    val engineer = (binding.engineersSpinner.selectedItem as SpinnerItem).value as User
    listener.onChange(engineer)
  }

  companion object {
    interface Listener {
      fun onChange(engineer: User)
    }
  }
}