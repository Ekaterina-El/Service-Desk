package com.elka.servicedesk.view.ui.engineer

import com.elka.servicedesk.view.ui.BaseLogsFragment

class EngineerLogsFragment: BaseLogsFragment() {
  override fun reloadLogs() {
    val divisionsIds = userViewModel.profile.value!!.divisionsLocal.map { it.id }
    logsViewModel.loadLogs(divisionsIds)
  }
}