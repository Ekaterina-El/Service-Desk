package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.AccidentStatus
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.model.splitAndSort
import com.elka.servicedesk.service.repository.AccidentsRepository
import kotlinx.coroutines.launch

class ReportViewModel(application: Application) : MenuBaseViewModel(application) {
	// region By divisions
	private val _accidents = MutableLiveData<List<Accident>>(listOf())
	val accidents get() = _accidents

	val filter = MutableLiveData("")
	private val _filteredAccidents = MutableLiveData<List<Accident>>(listOf())
	val filteredAccidents get() = _filteredAccidents

	fun loadAccidents(accidentIds: List<String>) {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.loadAccidents(accidentIds) { list ->
				_accidents.value = list.splitAndSort()
				filterAccidents()
			}
			removeWork(work)
		}
	}

	fun loadAccidentsOfDivisions(divisionsIds: List<String>) {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value =
				AccidentsRepository.loadAccidentsOfDivisions(divisionsIds, AccidentStatus.ACTIVE) { list ->
					_accidents.value = list.splitAndSort()
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

	private fun removeAccidentFromActiveById(accidentId: String) {
		val items = _accidents.value!!.toMutableList()
		items.removeIf { it.id == accidentId }
		_accidents.value = items
	}
	// endregion

}