package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.elka.servicedesk.databinding.ChangeUserDivisionBinding
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter
import com.elka.servicedesk.view.ui.BaseFragment

class ChangeUserDivisionDialog(
  context: Context,
  private val fragment: BaseFragment,
  private val listener: Listener
) : Dialog(context) {
  private lateinit var binding: ChangeUserDivisionBinding
  private var currentSelectedDivision: Division? = null

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = ChangeUserDivisionBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@ChangeUserDivisionDialog
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    setOnDismissListener { disagree() }
  }


  fun open(selectedDivision: Division?, divisions: List<Division>) {
    this@ChangeUserDivisionDialog.currentSelectedDivision = selectedDivision

    val spinnerAdapter = DivisionsAdapter(context, divisions)
    binding.divisionSpinner.adapter = spinnerAdapter

    fragment.selectItemOnSpinner(binding.divisionSpinner, divisions, selectedDivision)

    show()
  }
  fun disagree() {
    dismiss()
  }

  fun agree() {
    val division = binding.divisionSpinner.selectedItem as Division
    if (division == this.currentSelectedDivision) disagree()
    else listener.onSave(division)
  }

  companion object {
    interface Listener {
      fun onSave(division: Division)
    }
  }
}