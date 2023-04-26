package com.elka.servicedesk.view.ui.customer

import com.elka.servicedesk.view.ui.BaseLogsFragment

class CustomerLogsFragment : BaseLogsFragment() {
  override fun reloadLogs() {
    val divisionId = userViewModel.profile.value!!.divisionLocal!!.id
    logsViewModel.loadLogs(listOf(divisionId))
  }
}