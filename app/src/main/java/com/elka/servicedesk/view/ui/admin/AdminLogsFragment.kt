package com.elka.servicedesk.view.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.LogsFragmentBinding
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.view.list.logs.LogsAdapter
import com.elka.servicedesk.viewModel.LogsViewModel

class AdminLogsFragment : AdminBaseFragment() {
  private val logsViewModel by activityViewModels<LogsViewModel>()
  private lateinit var binding: LogsFragmentBinding

  private val logsAdapter by lazy { LogsAdapter() }
  private val logsObserver = Observer<List<Log>> {
    logsAdapter.setItems(it)
  }

  override val workObserver = Observer<List<Work>> {
    binding.swiper.isRefreshing = hasLoads
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    binding = LogsFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      viewModel = this@AdminLogsFragment.logsViewModel
      logsAdapter = this@AdminLogsFragment.logsAdapter
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val decorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    binding.logsList.addItemDecoration(decorator)

    val refresherColor = requireContext().getColor(R.color.accent)
    val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener { logsViewModel.loadLogs() }

    binding.swiper.setColorSchemeColors(refresherColor)
    binding.swiper.setOnRefreshListener(swipeRefreshListener)

    binding.layoutNoFound.message.text = getString(R.string.list_empty)
  }

  override fun onResume() {
    super.onResume()

    if (logsViewModel.logs.value!!.isEmpty()) logsViewModel.loadLogs()

    logsViewModel.work.observe(this, workObserver)
    logsViewModel.error.observe(this, errorObserver)
    logsViewModel.filteredLogs.observe(this, logsObserver)
    userViewModel.error.observe(this, errorObserver)
    userViewModel.work.observe(this, workObserver)
  }

  override fun onStop() {
    super.onStop()

    logsViewModel.work.removeObserver(workObserver)
    logsViewModel.error.removeObserver(errorObserver)
    logsViewModel.filteredLogs.removeObserver(logsObserver)

    userViewModel.error.removeObserver(errorObserver)
    userViewModel.work.removeObserver(workObserver)
  }
}