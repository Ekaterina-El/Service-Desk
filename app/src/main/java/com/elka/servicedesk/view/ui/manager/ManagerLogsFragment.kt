package com.elka.servicedesk.view.ui.manager

import com.elka.servicedesk.view.ui.BaseLogsFragment

class ManagerLogsFragment: BaseLogsFragment() {
  override fun reloadLogs() {
    logsViewModel.loadLogs()
  }
}