package com.elka.servicedesk.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.elka.servicedesk.databinding.ChangeEngineerDivisionsBinding
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.divisions.DivisionViewHolder
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter
import com.elka.servicedesk.view.list.divisions.DivisionsDinamicAdapter

class ChangeEngineerDivisionsDialog(
	context: Context,
	private val listener: Listener
) : Dialog(context) {
	private lateinit var binding: ChangeEngineerDivisionsBinding
	private lateinit var profile: User

	private val divisionsAdapterListener by lazy {
		object : DivisionViewHolder.Companion.Listener {
			override fun onDelete(division: Division) {
				divisionsAdapter.removeItem(division)
				updateCountOfDivisions()
			}
		}
	}

	fun updateCountOfDivisions() {
		binding.countOfDivisions = divisionsAdapter.getAllItems().size
	}

	private val divisionsAdapter: DivisionsDinamicAdapter by lazy {
		DivisionsDinamicAdapter(divisionsAdapterListener)
	}

	init {
		initDialog()
	}

	private fun initDialog() {
		binding = ChangeEngineerDivisionsBinding.inflate(LayoutInflater.from(context))
		binding.apply {
			master = this@ChangeEngineerDivisionsDialog
			divisionsAdapter = this@ChangeEngineerDivisionsDialog.divisionsAdapter
		}
		setContentView(binding.root)

		window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		setCancelable(true)

		setOnDismissListener { disagree() }
	}


	fun open(profile: User, divisions: List<Division>) {
		this@ChangeEngineerDivisionsDialog.profile = profile
		val userDivisions = profile.divisionsLocal.toMutableList()
		val divisionsSize = userDivisions.size
		divisionsAdapter.setItems(userDivisions)
		binding.countOfDivisions = divisionsSize

		val decorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
		binding.divisionsList.addItemDecoration(decorator)

		val spinnerAdapter = DivisionsAdapter(context, divisions)
		binding.divisionSpinner.adapter = spinnerAdapter

		show()
	}

	fun addDivision() {
		val division = binding.divisionSpinner.selectedItem as Division
		if (divisionsAdapter.getAllItems().contains(division)) return
		divisionsAdapter.addItem(division)
		updateCountOfDivisions()
	}

	fun disagree() {
		dismiss()
	}

	fun agree() {
		val divisions = divisionsAdapter.getAllItems()
		if (divisions.isEmpty()) return

		profile.divisionsLocal = divisions
		profile.divisionsId = divisions.map { it.id }
		listener.onSave(profile)
	}

	companion object {
		interface Listener {
			fun onSave(user: User)
		}
	}
}