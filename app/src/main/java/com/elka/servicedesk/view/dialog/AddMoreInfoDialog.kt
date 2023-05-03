package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AddAccidentMoreInfoDialogBinding
import com.elka.servicedesk.other.AccidentMoreInfo
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.service.model.User

class AddMoreInfoDialog(
	context: Context, private val listener: Listener
) : Dialog(context) {
	private lateinit var binding: AddAccidentMoreInfoDialogBinding
	private lateinit var user: User

	init {
		initDialog()
	}

	private fun initDialog() {
		binding = AddAccidentMoreInfoDialogBinding.inflate(LayoutInflater.from(context))
		binding.apply {
			master = this@AddMoreInfoDialog
		}
		setContentView(binding.root)

		window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		setCancelable(true)

		setOnDismissListener { disagree() }
	}


	fun open(user: User, title: String, hint: String) {
		binding.title.text = title
		binding.message.hint = hint
		binding.layoutMoreInformation.error = ""

		this.user = user
		show()
	}

	fun disagree() {
		binding.moreInformation.setText("")
		dismiss()
	}

	fun agree() {
		val moreInfo = binding.moreInformation.text.toString()
		if (moreInfo.isEmpty()) {
			binding.layoutMoreInformation.error = context.getString(R.string.is_require)
			return
		} else {
			binding.layoutMoreInformation.error = ""
		}

		listener.onSave(
			AccidentMoreInfo(
				message = moreInfo, user = user
			)
		)
	}

	companion object {
		interface Listener {
			fun onSave(accidentMoreInfo: AccidentMoreInfo)
		}
	}
}