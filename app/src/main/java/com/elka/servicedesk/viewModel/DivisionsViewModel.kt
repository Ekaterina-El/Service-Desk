package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.DivisionsRepository
import kotlinx.coroutines.launch

class DivisionsViewModel(application: Application) : BaseViewModel(application) {
  private val _divisions = MutableLiveData<List<Division>>(listOf())
  val divisions get() = _divisions

  val filter = MutableLiveData("")
  private val _filteredDivisions = MutableLiveData<List<Division>>(listOf())
  val filteredDivisions get() = _filteredDivisions

  fun addDivision(division: Division, editor: User) {
    val work = Work.ADD_DIVISION
    addWork(work)

    viewModelScope.launch {
      _error.value = DivisionsRepository.addDivision(division, editor) { newDivision ->
        addDivisionsToLocal(newDivision)
        filterDivisions()
      }

      removeWork(work)
    }
  }

  fun removeDivision(division: Division, editor: User) {
    val work = Work.REMOVE_DIVISION
    addWork(work)

    viewModelScope.launch {
      _error.value = DivisionsRepository.removeDivision(division, editor) {
        removeDivisionsFromLocal(division)
        filterDivisions()
      }

      removeWork(work)
    }
  }

  fun filterDivisions() {
    val items = _divisions.value!!
    val filter = filter.value!!

    _filteredDivisions.value = when(filter) {
      "" -> items
      else -> items.filterBy(filter)
    }
  }

  fun clearFilterDivisions() {
    filter.value = ""
    filterDivisions()
  }

  private fun addDivisionsToLocal(newDivision: Division) {
    val divisions = _divisions.value!!.toMutableList()
    divisions.add(newDivision)
    _divisions.value = divisions
  }

  private fun removeDivisionsFromLocal(division: Division) {
    val divisions = _divisions.value!!.toMutableList()
    divisions.remove(division)
    _divisions.value = divisions
  }

  fun loadDivisions() {
    val work = Work.LOAD_DIVISIONS
    addWork(work)

    viewModelScope.launch {
      _error.value = DivisionsRepository.getAllDivisions { divisions ->
        _divisions.value = divisions.sortedBy { it.name }
        filterDivisions()
      }
      removeWork(work)
    }
  }
}