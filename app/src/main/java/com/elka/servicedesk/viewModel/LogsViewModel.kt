package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.LogsRepository
import kotlinx.coroutines.launch

class LogsViewModel(application: Application) : BaseViewModel(application) {
  private val _logs = MutableLiveData<List<Log>>(listOf())
  val logs get() = _logs

  val filter = MutableLiveData("")
  private val _filteredLogs = MutableLiveData<List<Log>>(listOf())
  val filteredLogs get() = _filteredLogs

  fun loadLogs() {
    val work = Work.LOAD_LOGS
    addWork(work)

    viewModelScope.launch {
      _error.value = LogsRepository.loadAllLogs {
        _logs.value = it
        filterLogs()
      }
      removeWork(work)
    }
  }

  fun filterLogs() {
    val items = _logs.value!!
    val filter = filter.value!!

    _filteredLogs.value = when (filter) {
      "" -> items
      else -> items.filterBy(filter)
    }
  }

  fun clearFilterLogs() {
    filter.value = ""
    filterLogs()
  }

  fun clear() {
    _externalAction.value = null
    _error.value = null
    filter.value = ""
    _logs.value = listOf()
    _filteredLogs.value = listOf()
    clearWork()
  }
}
