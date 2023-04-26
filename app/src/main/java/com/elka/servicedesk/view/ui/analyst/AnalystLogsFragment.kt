package com.elka.servicedesk.view.ui.analyst

import com.elka.servicedesk.view.ui.BaseLogsFragment

class AnalystLogsFragment: BaseLogsFragment() {
  override fun reloadLogs() {
    val divisionsIds = userViewModel.profile.value!!.divisionsLocal.map { it.id }
    logsViewModel.loadLogs(divisionsIds)
  }
}