package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.Work
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.repository.LogsRepository
import kotlinx.coroutines.launch

class IncidentsRequestsViewModel(application: Application) : BaseViewModel(application) {
 /* private val _accidents = MutableLiveData<List<Accidents>>(listOf())
  val accidents get() = _accidents

  val filter = MutableLiveData("")
  private val _filteredAccidents = MutableLiveData<List<Accidents>>(listOf())
  val filteredAccidents get() = _filteredAccidents
  */
}