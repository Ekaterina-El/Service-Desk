package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.elka.servicedesk.databinding.ConfirmDialogBinding

class ConfirmDialog(context: Context) : Dialog(context) {
  private lateinit var binding: ConfirmDialogBinding
  private var listener: Listener? = null

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = ConfirmDialogBinding.inflate(LayoutInflater.from(context))
    binding.apply {
      master = this@ConfirmDialog
    }
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

  }

  fun open(title: String, message: String, listener: Listener) {
    binding.title = title
    binding.message = message
    this.listener = listener
    show()
  }

  fun close() {
    binding.title = ""
    binding.message = ""
    dismiss()
  }

  fun disagree() {
    listener?.disagree()
  }

  fun agree() {
    listener?.agree()
  }

  companion object {
    interface Listener {
      fun agree()
      fun disagree()
    }
  }
}