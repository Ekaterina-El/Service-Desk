package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.model.splitAndSort
import com.elka.servicedesk.service.repository.AccidentsRepository
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
      _error.value = AccidentsRepository.loadAccidentsOfDivisions(divisionsIds) { list ->
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

  fun addAccident(accident: Accident) {
    val items = _accidents.value!!.toMutableList()
    items.add(accident)
    _accidents.value = items
    filterAccidents()
  }
  // endregion

  // region Single Accident

  fun reloadCurrentAccident() {
    if (_currentLoadedAccidentId == null) return

    val work = Work.LOAD_ACCIDENT
    addWork(work)

    viewModelScope.launch {
      _error.value = AccidentsRepository.loadAccident(_currentLoadedAccidentId!!) { accident ->
        _currentAccident.value = accident
      }
      removeWork(work)
    }
  }

  fun clearCurrentAccident() {
    _currentAccident.value = null
    _currentLoadedAccidentId = null
  }

  private var _currentLoadedAccidentId: String? = null
  private val _currentAccident = MutableLiveData<Accident?>(null)
  val currentAccident get() = _currentAccident

  fun loadCurrentOpenAccident(accidentId: String) {
    _currentLoadedAccidentId = accidentId
    reloadCurrentAccident()
  }
  // endregion
}