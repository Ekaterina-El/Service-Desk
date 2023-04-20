package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.repository.DivisionsRepository
import kotlinx.coroutines.launch

class DivisionsViewModel(application: Application) : BaseViewModel(application) {
  private val _divisions = MutableLiveData<List<Division>>(listOf())
  val divisions get() = _divisions

  fun addDivision(division: Division) {
    val work = Work.ADD_DIVISION
    addWork(work)

    viewModelScope.launch {
      _error.value = DivisionsRepository.addDivision(division) { newDivision ->
        addDivisionsToLocal(newDivision)
      }

      removeWork(work)
    }
  }

  private fun addDivisionsToLocal(newDivision: Division) {
    val divisions = _divisions.value!!.toMutableList()
    divisions.add(newDivision)
    _divisions.value = divisions
  }

  fun loadDivisions() {
    val work = Work.LOAD_DIVISIONS
    addWork(work)

    viewModelScope.launch {
      _error.value = DivisionsRepository.getAllDivisions { divisions ->
        _divisions.value = divisions.sortedBy { it.name }
      }
      removeWork(work)
    }
  }
}