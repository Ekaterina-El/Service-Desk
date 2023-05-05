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
	// region Requests by divisions
	private val _requestsByDivisions = MutableLiveData<List<Accident>>(listOf())
	val requestsByDivisions get() = _requestsByDivisions

	val requestsByDivisionsFilter = MutableLiveData("")
	private val _requestsByDivisionsFiltered = MutableLiveData<List<Accident>>(listOf())
	val requestsByDivisionsFiltered get() = _requestsByDivisionsFiltered

	fun loadRequests() {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.loadAllRequests { list ->
				_requestsByDivisions.value = list.splitAndSort()
				filterRequestsByDivisions()
			}
			removeWork(work)
		}
	}

	fun filterRequestsByDivisions() {
		val items = _requestsByDivisions.value!!
		val filter = requestsByDivisionsFilter.value!!

		_requestsByDivisionsFiltered.value = when (filter) {
			"" -> items
			else -> items.filterBy(filter)
		}
	}

	fun clearFilterRequestsByDivisions() {
		requestsByDivisionsFilter.value = ""
		filterRequestsByDivisions()
	}
	// endregion

	// region Incidents with missed deadlines by divisions
	private val _incidentsWithMissedDeadline = MutableLiveData<List<Accident>>(listOf())
	val incidentsWithMissedDeadline get() = _incidentsWithMissedDeadline

	val incidentsWithMissedDeadlineFilter = MutableLiveData("")
	private val _incidentsWithMissedDeadlineFiltered = MutableLiveData<List<Accident>>(listOf())
	val incidentsWithMissedDeadlineFiltered get() = _incidentsWithMissedDeadlineFiltered

	fun loadIncidentsWithMissedDeadline() {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.loadIncidentsWithMissedDeadline { list ->
				_incidentsWithMissedDeadline.value = list.splitAndSort()
				filterIncidentsWithMissedDeadline()
			}
			removeWork(work)
		}
	}

	fun filterIncidentsWithMissedDeadline() {
		val items = _incidentsWithMissedDeadline.value!!
		val filter = incidentsWithMissedDeadlineFilter.value!!

		_incidentsWithMissedDeadlineFiltered.value = when (filter) {
			"" -> items
			else -> items.filterBy(filter)
		}
	}

	fun clearFilterIncidentsWithMissedDeadline() {
		incidentsWithMissedDeadlineFilter.value = ""
		filterIncidentsWithMissedDeadline()
	}
	// endregion
}