package com.elka.servicedesk.view.ui.admin

import com.elka.servicedesk.view.ui.BaseLogsFragment

class AdminLogsFragment: BaseLogsFragment() {
  override fun reloadLogs() {
    logsViewModel.loadLogs()
  }
}