package com.elka.servicedesk.view.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.elka.servicedesk.MainActivity
import com.elka.servicedesk.R
import com.elka.servicedesk.other.*
import com.google.android.material.textfield.TextInputLayout
import java.util.HashMap

abstract class BaseFragment: Fragment() {
  protected fun restartApp() {
    try {
      val i =
        requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
          ?: return
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      requireActivity().finish()
      startActivity(i)
    } catch (_: Exception) {
    }
  }

  protected val navController by lazy { findNavController() }
  protected val activity by lazy { requireActivity() as MainActivity }

  val errorObserver = Observer<ErrorApp?> {
    if (it != null) activity.informDialog.open(
      getString(R.string.error_title),
      getString(it.messageRes)
    )
  }

  protected open val workObserver = Observer<List<Work>> {
    if (it.isEmpty()) hideLoadingDialog() else showLoadingDialog()
  }

  private fun showLoadingDialog() {
    activity.loadingDialog.show()
  }

  private fun hideLoadingDialog() {
    activity.loadingDialog.dismiss()
  }

  private val clipboard by lazy { activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
  fun copyToClipboard(data: String) {
    val label = getString(R.string.data_copied)
    val clip = ClipData.newPlainText(label, data)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(requireContext(), label, Toast.LENGTH_SHORT).show()
  }


  fun getCredentials(): Credentials? {
    val credentials =
      activity.sharedPreferences.getString(Constants.CREDENTIALS, null) ?: return null
    val parts = credentials.split(Constants.SEPARATOR)
    return when (parts.size) {
      2 -> Credentials(parts[0], parts[1])
      else -> null
    }
  }

  fun setCredentials(credentials: Credentials?) {
    val edit = activity.sharedPreferences.edit()
    val s = when (credentials) {
      null -> ""
      else -> "${credentials.email}${Constants.SEPARATOR}${credentials.password}"
    }
    edit.putString(Constants.CREDENTIALS, s)
    edit.apply()
  }

  fun showErrors(errors: List<FieldError>?, fields: HashMap<Field, Any>) {
    for (field in fields) {
      val error = errors?.firstOrNull { it.field == field.key }
      val errorStr = error?.let { getString(it.errorType!!.messageRes) } ?: ""

      when (val f = field.value) {
        is TextInputLayout -> f.error = errorStr
        is TextView -> f.text = errorStr
        else -> Unit
      }
    }
  }


  fun selectItemOnSpinner(spinner: Spinner, items: List<SpinnerItem>, value: Any?) {
    val pos =  if (value != null) {
      items.indexOfFirst { it.value == value }
    } else 0
    spinner.setSelection(pos)
  }

  fun showLoadingErrorMessage() {
    Toast.makeText(requireContext(), getString(R.string.await_loading), Toast.LENGTH_SHORT).show()
  }
}