package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elka.servicedesk.databinding.InformDialogBinding

class InformDialog(context: Context) : Dialog(context) {
  private lateinit var binding: InformDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = InformDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    setCancelable(true)

    binding.buttonContinue.setOnClickListener { dismiss() }
  }

  fun open(
    title: String,
    message: String,
    hint: String? = null,
    listener: Listener? = null,
    copyMessage: String? = null,
    onButtonListener: (() -> Unit)? = null,
    cancelable: Boolean = true
  ) {
    setCancelable(cancelable)

    binding.textViewTitle.text = title
    binding.textViewMessage.text = message


    binding.buttonContinue.setOnClickListener { dismiss() }
    onButtonListener?.apply {
      binding.buttonContinue.setOnClickListener {
        onButtonListener()
        dismiss()
      }
    }

    listener?.apply { binding.textViewMessage.setOnClickListener { this.copyMessage(copyMessage!!) } }

    if (hint != null) {
      binding.textViewHint.text = hint
      binding.textViewHint.visibility = View.VISIBLE
    } else {
      binding.textViewHint.visibility = View.GONE
    }

    show()
  }

  fun close() {
    binding.textViewMessage.setOnClickListener(null)
    binding.textViewMessage.text = ""
    binding.textViewHint.text = ""
    dismiss()
  }

  companion object {
    interface Listener {
      fun copyMessage(message: String)
    }
  }

}