package com.elka.servicedesk.view.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ReportFragmentBinding

class ReportFragment : ReportBaseFragment() {
	private lateinit var binding: ReportFragmentBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = ReportFragmentBinding.inflate(layoutInflater, container, false)
		binding.apply {
			lifecycleOwner = viewLifecycleOwner
			viewModel = this@ReportFragment.reportViewModel
		}
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)

		val navController =
			(childFragmentManager.findFragmentById(R.id.reportContainer) as NavHostFragment)
				.navController
		binding.bottomMenu.setupWithNavController(navController)
	}
}