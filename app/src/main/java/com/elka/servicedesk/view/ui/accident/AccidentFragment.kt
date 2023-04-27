package com.elka.servicedesk.view.ui.accident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AccidentFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel

class AccidentFragment : UserBaseFragment() {
  private lateinit var binding: AccidentFragmentBinding
  private val accidentsViewModel by activityViewModels<AccidentsViewModel>()


  private val works = listOf(
    Work.LOAD_ACCIDENT
  )

  override val workObserver = Observer<List<Work>> {
    val w1 = userViewModel.work.value!!.toMutableList()
    val w2 = accidentsViewModel.work.value!!
    w1.addAll(w2)
    val hasLoads = getHasLoads(w1, works)

    binding.swiper.isRefreshing = hasLoads
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = AccidentFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@AccidentFragment
      viewModel = this@AccidentFragment.accidentsViewModel
    }

    return binding.root
  }

  fun goBack() {
    accidentsViewModel.clearCurrentAccident()
    navController.popBackStack()
  }

  private val onBackPressedCallback by lazy {
    object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        goBack()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)

    val refresherColor = requireContext().getColor(R.color.accent)
    val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener { reloadAccident() }

    binding.swiper.setColorSchemeColors(refresherColor)
    binding.swiper.setOnRefreshListener(swipeRefreshListener)
  }

  private fun reloadAccident() {
    accidentsViewModel.reloadCurrentAccident()
  }

  override fun onResume() {
    super.onResume()

    userViewModel.error.observe(this, errorObserver)
    userViewModel.work.observe(this, workObserver)
    accidentsViewModel.error.observe(this, errorObserver)
    accidentsViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()

    userViewModel.error.removeObserver(errorObserver)
    userViewModel.work.removeObserver(workObserver)
    accidentsViewModel.error.removeObserver(errorObserver)
    accidentsViewModel.work.removeObserver(workObserver)
  }
}