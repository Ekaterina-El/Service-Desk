package com.elka.servicedesk.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Work

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
  protected val _externalAction = MutableLiveData<Action?>(null)
  val externalAction: LiveData<Action?> = _externalAction

  protected val _error = MutableLiveData<ErrorApp?>(null)
  val error: LiveData<ErrorApp?> = _error

  private val workStack = MutableLiveData<List<Work>>(listOf())
  val work: LiveData<List<Work>> get() = workStack

  fun clearWork() {
    workStack.value = listOf()
  }

  protected fun addWork(work: Work) {
    changeWorks(work, Action.ADD)
  }

  protected fun removeWork(work: Work) {
    changeWorks(work, Action.REMOVE)
  }

  private fun changeWorks(work: Work, action: Action) {
    val works = workStack.value!!.toMutableList()
    when (action) {
      Action.ADD -> works.add(work)
      Action.REMOVE -> works.remove(work)
      else -> {}
    }

    workStack.value = works
  }
}