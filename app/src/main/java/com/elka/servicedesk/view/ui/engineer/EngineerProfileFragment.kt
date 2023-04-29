package com.elka.servicedesk.view.ui.engineer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ChipItemBinding
import com.elka.servicedesk.databinding.EngineerProfileFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.User
import com.google.android.material.chip.Chip

class EngineerProfileFragment : EngineerBaseFragment() {
	private lateinit var binding: EngineerProfileFragmentBinding

	private val profileObserver = Observer<User?> { user ->
		if (user == null) return@Observer
		showDivisions(user.divisionsLocal.map { it.name })
	}

	override val workObserver = Observer<List<Work>> {
		binding.swiper.isRefreshing = hasLoads
	}

	override val externalActionObserver = Observer<Action?> {
		if (it == null) return@Observer
		super.externalActionObserver.onChanged(it)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = EngineerProfileFragmentBinding.inflate(layoutInflater, container, false)
		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			master = this@EngineerProfileFragment
			viewModel = this@EngineerProfileFragment.userViewModel
		}

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val refresherColor = requireContext().getColor(R.color.accent)
		val swipeRefreshListener =
			SwipeRefreshLayout.OnRefreshListener { userViewModel.loadCurrentUserProfile() }

		binding.swiper.setColorSchemeColors(refresherColor)
		binding.swiper.setOnRefreshListener(swipeRefreshListener)
	}


	override fun onResume() {
		super.onResume()

		divisionsViewModel.work.observe(this, workObserver)
		userViewModel.work.observe(this, workObserver)
		userViewModel.error.observe(this, errorObserver)
		userViewModel.externalAction.observe(this, externalActionObserver)
		userViewModel.profile.observe(this, profileObserver)
	}

	override fun onStop() {
		super.onStop()

		divisionsViewModel.work.removeObserver(workObserver)
		userViewModel.work.removeObserver(workObserver)
		userViewModel.error.removeObserver(errorObserver)
		userViewModel.externalAction.removeObserver(externalActionObserver)
		userViewModel.profile.removeObserver(profileObserver)
	}

	private fun showDivisions(divisions: List<String>) {
		val context = binding.root.context
		val layoutInflater = LayoutInflater.from(context)

		binding.divisionChips.removeAllViews()
		divisions.forEach {
			val chip = createChip(layoutInflater, it)
			binding.divisionChips.addView(chip)
		}
	}

	private fun createChip(layoutInflater: LayoutInflater, value: String): Chip {
		val chip = ChipItemBinding.inflate(layoutInflater).root as Chip
		chip.text = value
		return chip
	}
}

