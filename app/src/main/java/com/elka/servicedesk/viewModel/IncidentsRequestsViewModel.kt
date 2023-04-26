package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.AccidentsRepository
import kotlinx.coroutines.launch

class IncidentsRequestsViewModel(application: Application) : BaseViewModel(application) {
  private val _accidents = MutableLiveData<List<Accident>>(listOf())
  val accidents get() = _accidents

  val filter = MutableLiveData("")
  private val _filteredAccidents = MutableLiveData<List<Accident>>(listOf())
  val filteredAccidents get() = _filteredAccidents

  fun loadAccidents(accidentIds: List<String>) {
    val work = Work.LOAD_ACCIDENTS
    addWork(work)

    viewModelScope.launch {
      _error.value = AccidentsRepository.loadAccidents(accidentIds) {
        _accidents.value = it
        filterAccidents()
      }
      removeWork(work)
    }
  }

  fun filterAccidents() {
    val items = _accidents.value!!
    val filter = filter.value!!

    _filteredAccidents.value = when (filter) {
      "" -> items
      else -> items.filterBy(filter)
    }
  }

  fun clearFilterAccidents() {
    filter.value = ""
    filterAccidents()
  }

  fun clear() {
    _externalAction.value = null
    _error.value = null
    filter.value = ""
    _accidents.value = listOf()
    _filteredAccidents.value = listOf()
    clearWork()
  }
}