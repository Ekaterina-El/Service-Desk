package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData

abstract class MenuBaseViewModel(application: Application) : BaseViewModel(application) {
  private val _menuStatus = MutableLiveData(true)
  val menuStatus get() = _menuStatus
  fun setMenuStatus(newStatus: Boolean) {
    _menuStatus.value = newStatus
  }
}