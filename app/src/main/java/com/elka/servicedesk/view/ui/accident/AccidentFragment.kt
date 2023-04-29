package com.elka.servicedesk.view.ui.accident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AccidentFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.view.list.images.ImageItem
import com.elka.servicedesk.view.list.images.ImagesAdapter
import com.elka.servicedesk.view.list.logs.LogsAdapter
import com.elka.servicedesk.view.ui.UserBaseFragment
import com.elka.servicedesk.viewModel.AccidentsViewModel

class AccidentFragment : UserBaseFragment() {
  private lateinit var binding: AccidentFragmentBinding
  private val accidentsViewModel by activityViewModels<AccidentsViewModel>()

  private val logsAdapter by lazy { LogsAdapter() }

  private val accidentLogsObserver = Observer<List<Log>> {
    logsAdapter.setItems(it)
  }


  private val imagesAdapter: ImagesAdapter by lazy {
    ImagesAdapter(null, null)
  }

  private val accidentObserver = Observer<Accident?>{accident ->
    if (accident == null) return@Observer

    val items = accident.photosURL.map { ImageItem(type = ImagesAdapter.TYPE_ITEM, it) }.toMutableList()
    imagesAdapter.setItems(items)
  }

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
      imagesAdapter = this@AccidentFragment.imagesAdapter
      logsAdapter = this@AccidentFragment.logsAdapter
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

    // get args
    val args = AccidentFragmentArgs.fromBundle(requireArguments())
    accidentsViewModel.loadCurrentOpenAccident(args.accidentId)

    // add decorator to list
    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.logsList.addItemDecoration(decorator)

    // add on back press listener
    activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)

    // work with swipe refresher
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
    accidentsViewModel.currentAccident.observe(this, accidentObserver)
    accidentsViewModel.currentAccidentLogs.observe(this, accidentLogsObserver)
  }

  override fun onStop() {
    super.onStop()

    userViewModel.error.removeObserver(errorObserver)
    userViewModel.work.removeObserver(workObserver)
    accidentsViewModel.error.removeObserver(errorObserver)
    accidentsViewModel.work.removeObserver(workObserver)
    accidentsViewModel.currentAccident.removeObserver(accidentObserver)
    accidentsViewModel.currentAccidentLogs.removeObserver(accidentLogsObserver)

  }
}