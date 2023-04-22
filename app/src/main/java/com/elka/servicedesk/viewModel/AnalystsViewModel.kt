package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.launch

class AnalystsViewModel(application: Application) : BaseUserViewModel(application) {
  private val _divisions = MutableLiveData<List<Division>>(listOf())
  val divisions get() = _divisions

  fun addDivision(division: Division) {
    if (_divisions.value!!.contains(division)) return

    val items = _divisions.value!!.toMutableList()
    items.add(division)
    _divisions.value = items
  }

  override fun clearDialog() {
    super.clearDialog()
    _divisions.value = listOf()
  }

  fun removeDivision(division: Division) {
    val items = _divisions.value!!.toMutableList()
    items.remove(division)
    _divisions.value = items
  }

  override val userRole = Role.ANALYST

}

