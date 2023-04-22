package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elka.servicedesk.other.*
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.service.model.filterBy
import com.elka.servicedesk.service.repository.UserRepository
import kotlinx.coroutines.launch

class AnalystsViewModel(application: Application) : BaseUserViewModel(application) {
  override val userRole = Role.ANALYST

}

