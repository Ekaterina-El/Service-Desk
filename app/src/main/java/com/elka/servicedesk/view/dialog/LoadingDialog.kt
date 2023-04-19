package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.LoaderDialogBinding

class LoadingDialog(context: Context): Dialog(context, R.style.AppTheme_FullScreenDialog) {
  private lateinit var binding: LoaderDialogBinding

  init {
    initDialog()
  }

  private fun initDialog() {
    binding = LoaderDialogBinding.inflate(LayoutInflater.from(context))
    setContentView(binding.root)

    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    setCancelable(false)
  }
}