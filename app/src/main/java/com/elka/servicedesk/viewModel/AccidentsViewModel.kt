package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.*
import com.elka.servicedesk.service.repository.AccidentsRepository
import com.elka.servicedesk.service.repository.LogsRepository
import kotlinx.coroutines.launch

class AccidentsViewModel(application: Application) : BaseViewModelWithFields(application) {
	// region All accidents
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

	// region Engineer accidents
	private val _engineerAccidents = MutableLiveData<List<Accident>>(listOf())
	val engineerAccidents get() = _engineerAccidents

	val engineerFilter = MutableLiveData("")
	private val _engineerFilteredAccidents = MutableLiveData<List<Accident>>(listOf())
	val engineerFilteredAccidents get() = _engineerFilteredAccidents

	fun loadEngineerAccidents(engineerId: String) {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.loadAccidentsOfEngineer(engineerId) {
				_engineerAccidents.value = it.splitAndSort()
				filterEngineerAccidents()
			}
			removeWork(work)
		}
	}

	fun filterEngineerAccidents() {
		val items = _engineerAccidents.value!!
		val filter = engineerFilter.value!!

		_engineerFilteredAccidents.value = when (filter) {
			"" -> items
			else -> items.filterBy(filter)
		}
	}

	fun clearFilterEngineerAccidents() {
		engineerFilter.value = ""
		filterEngineerAccidents()
	}

	private fun addAccidentToInWork(accident: Accident) {
		val items = _engineerAccidents.value!!.toMutableList()
		items.add(accident)
		_engineerAccidents.value = items
	}
	// endregion

	// region Admin failed accidents
	private val _failedAccidents = MutableLiveData<List<Accident>>(listOf())
	val failedAccidents get() = _failedAccidents

	val failedFilter = MutableLiveData("")
	private val _failedFilteredAccidents = MutableLiveData<List<Accident>>(listOf())
	val failedFilteredAccidents get() = _failedFilteredAccidents

	fun loadFailedAccidents() {
		val work = Work.LOAD_ACCIDENTS
		addWork(work)

		viewModelScope.launch {
			_error.value =
				AccidentsRepository.loadAllAccidentsWithStatus(status = AccidentStatus.ESCALATION) { excalations ->
					_failedAccidents.value = excalations.splitAndSort()
					filterFailedAccidents()
				}
			removeWork(work)
		}
	}

	fun filterFailedAccidents() {
		val items = _failedAccidents.value!!
		val filter = failedFilter.value!!

		_failedFilteredAccidents.value = when (filter) {
			"" -> items
			else -> items.filterBy(filter)
		}
	}

	fun clearFilterFailedAccidents() {
		failedFilter.value = ""
		filterFailedAccidents()
	}
	// endregion

	// region Add Accident
	val subject = MutableLiveData("")
	val message = MutableLiveData("")

	override val fields: HashMap<Field, MutableLiveData<Any?>> = hashMapOf(
		Pair(Field.SUBJECT, subject as MutableLiveData<Any?>),
		Pair(Field.MESSAGE, message as MutableLiveData<Any?>),

		)

	private var _accidentCategory: AccidentCategory? = null
	fun setAccidentCategory(accidentCategory: AccidentCategory) {
		_accidentCategory = accidentCategory
	}

	var _urgencyCategory: UrgencyCategory? = null
	fun setUrgencyCategory(urgencyCategory: UrgencyCategory) {
		_urgencyCategory = urgencyCategory
	}

	fun getNewAccident(profile: User, accidentType: AccidentType) = Accident(
		type = accidentType,
		subject = subject.value!!,
		message = message.value!!,
		urgency = _urgencyCategory!!,
		category = _accidentCategory!!,
		divisionId = profile.divisionId,
		userId = profile.id,
		divisionLocal = profile.divisionLocal!!.copy(),
		userLocal = profile.copy(),
		status = AccidentStatus.ACTIVE,
		photosURL = _newAccidentImages.value!!
	)

	private val _addedAccident = MutableLiveData<Accident?>(null)
	val addedAccident get() = _addedAccident

	private val _newAccidentImages = MutableLiveData<List<String>>(listOf())
	val newAccidentImages get() = _newAccidentImages

	fun addPhoto(uri: String) {
		val items = _newAccidentImages.value!!.toMutableList()
		items.add(uri)
		_newAccidentImages.value = items
	}

	fun removePhoto(uri: String) {
		val items = _newAccidentImages.value!!.toMutableList()
		items.remove(uri)
		_newAccidentImages.value = items
	}

	fun clear() {
		_externalAction.value = null
		_error.value = null
		filter.value = ""
		_accidents.value = listOf()
		_filteredAccidents.value = listOf()
		clearDialog()
		clearWork()
	}

	fun tryAddAccident(profile: User, accident: Accident) {
		if (!checkFields()) return
		val work = Work.ADD_ACCIDENT
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.addAccident(profile, accident) {
				_addedAccident.value = it
			}
			removeWork(work)
		}
	}

	fun clearDialog() {
		_newAccidentImages.value = listOf()
		_addedAccident.value = null
		message.value = ""
		subject.value = ""
		_urgencyCategory = null
		_accidentCategory = null
	}

	fun addAccidentToEngineer(accident: Accident) {
		val items = _engineerAccidents.value!!.toMutableList()
		items.add(accident)
		_engineerAccidents.value = items
		filterEngineerAccidents()
	}

	private fun removeAccidentFromEngineerAccidents(accident: Accident) {
		val items = _engineerAccidents.value!!.toMutableList()
		items.removeIf { it.id == accident.id }
		_engineerAccidents.value = items
		filterEngineerAccidents()
	}
	// endregion

	// region Single Accident
	fun reloadCurrentAccident() {
		if (_currentLoadedAccidentId == null) return

		val work = Work.LOAD_ACCIDENT
		addWork(work)

		viewModelScope.launch {
			_error.value = AccidentsRepository.loadAccident(_currentLoadedAccidentId!!) { accident ->
				_error.value = LogsRepository.loadAccidentLogs(accident.id) { logs ->
					_currentAccidentLogs.value = logs.sortedByDescending { it.date }
					_currentAccident.value = accident
				}
			}
			removeWork(work)
		}
	}

	fun clearCurrentAccident() {
		_currentAccident.value = null
		_currentAccidentLogs.value = listOf()
		_currentLoadedAccidentId = null
	}

	private var _currentLoadedAccidentId: String? = null
	private val _currentAccident = MutableLiveData<Accident?>(null)
	val currentAccident get() = _currentAccident

	private val _currentAccidentLogs = MutableLiveData<List<Log>>(listOf())
	val currentAccidentLogs get() = _currentAccidentLogs

	fun loadCurrentOpenAccident(accidentId: String) {
		_currentLoadedAccidentId = accidentId
		reloadCurrentAccident()
	}

	private fun updateCurrentAccidentInLists() {
		val currentAccident = _currentAccident.value!!
		_accidents.value =
			_accidents.value!!.map { if (it.id == currentAccident.id) currentAccident else it }
		_engineerAccidents.value =
			_engineerAccidents.value!!.map { if (it.id == currentAccident.id) currentAccident else it }
		_failedAccidents.value =
			_failedAccidents.value!!.filter { it.status == AccidentStatus.ESCALATION }
				.map { if (it.id == currentAccident.id) currentAccident else it }

		filterAccidents()
		filterEngineerAccidents()
		filterFailedAccidents()
	}

	fun acceptCurrentAccidentToWork(engineer: User, onAccept: () -> Unit) {
		val work = Work.ACCEPT_ACCIDENT_TO_WORK
		addWork(work)

		viewModelScope.launch {
			val accident = _currentAccident.value!!
			val division = accident.divisionLocal!!

			_error.value = AccidentsRepository.acceptAccidentToWork(accident, engineer, division) { log ->
				// delete accident from allAccidents
				removeAccidentFromActiveById(accident.id)

				// add accident to engineerAccidents
				accident.status = AccidentStatus.IN_WORK
				accident.engineerId = engineer.id
				accident.engineerLocal = engineer

				addLog(log)

				addAccidentToInWork(accident)

				_currentAccident.value = accident
				updateCurrentAccidentInLists()
				onAccept()
			}
			removeWork(work)
		}
	}

	private fun addLog(log: Log) {
		val logs = _currentAccidentLogs.value!!.toMutableList()
		logs.add(log)
		_currentAccidentLogs.value = logs.sortedByDescending { it.date }
	}

	fun closeAccident(onClose: () -> Unit) {
		val work = Work.CLOSE_ACCIDENT
		addWork(work)

		viewModelScope.launch {
			val accident = _currentAccident.value!!.copy()
			val division = accident.divisionLocal!!
			val engineer = accident.engineerLocal!!

			_error.value =
				AccidentsRepository.closeAccidentFromEngineer(accident, engineer, division) { log ->
					// add accident to engineerAccidents
					removeAccidentFromEngineerAccidents(accident)

					addLog(log)

					accident.status = AccidentStatus.CLOSED
					_currentAccident.value = accident

					updateCurrentAccidentInLists()
					onClose()
				}
			removeWork(work)
		}
	}

	fun acceptCloseAccidentFromUser(user: User, onClose: () -> Unit) {
		val work = Work.CLOSE_ACCIDENT
		addWork(work)

		viewModelScope.launch {
			val accident = _currentAccident.value!!.copy(status = AccidentStatus.READY)
			val division = accident.divisionLocal!!

			_error.value = AccidentsRepository.closeAccidentFromUser(accident, user, division) { log ->
				addLog(log)

				_currentAccident.value = accident

				updateCurrentAccidentInLists()
				onClose()
			}
			removeWork(work)
		}

	}

	fun addAccidentMoreInfo(accidentMoreInfo: AccidentMoreInfo, onAdded: () -> Unit) {
		val work = Work.ADD_MORE_INFORMATION
		addWork(work)

		viewModelScope.launch {
			val newStatus =
				if (accidentMoreInfo.user!!.role == Role.USER) AccidentStatus.IN_WORK else AccidentStatus.WAITING
			val accident = _currentAccident.value!!.copy(status = newStatus)

			_error.value = AccidentsRepository.addAccidentMoreInfo(accident, accidentMoreInfo) { log ->
				addLog(log)

				val moreInfo = accident.moreInfo.toMutableList()
				moreInfo.add(accidentMoreInfo)
				accident.moreInfo = moreInfo.sortedBy { it.date }
				_currentAccident.value = accident

				updateCurrentAccidentInLists()
				onAdded()
			}
			removeWork(work)
		}

	}

	fun sendExcalation(accidentMoreInfo: AccidentMoreInfo, onSuccess: () -> Unit) {
		val work = Work.EXCALATION
		addWork(work)

		viewModelScope.launch {
			val accident = currentAccident.value!!.copy()
			val user = accidentMoreInfo.user!!.copy()
			val reason = accidentMoreInfo.message

			_error.value = AccidentsRepository.sendEscalation(accident, reason, user) { log ->
				addLog(log)

				// change status
				accident.status = AccidentStatus.ESCALATION

				// add reason on currentAccident
				accident.reasonOfExcalation = reason
				accident.senderOfExcalation = user

				_currentAccident.value = accident
				updateCurrentAccidentInLists()
				onSuccess()
			}

			removeWork(work)
		}
	}

	fun changeEngineer(newEngineer: User, editor: User) {
		val work = Work.CHANGE_ENGINEER
		addWork(work)

		viewModelScope.launch {
			val accident = currentAccident.value!!.copy()
			_error.value = AccidentsRepository.changeEngineer(accident, newEngineer, editor) { log: Log ->
				addLog(log)

				// change status
				accident.status = AccidentStatus.IN_WORK

				// add reason on currentAccident
				accident.reasonOfExcalation = ""
				accident.senderOfExcalation = null

				// update engineer
				accident.engineerId = newEngineer.id
				accident.engineerLocal = newEngineer

				_currentAccident.value = accident
				updateCurrentAccidentInLists()
			}
			removeWork(work)
		}
	}
	// endregion
}