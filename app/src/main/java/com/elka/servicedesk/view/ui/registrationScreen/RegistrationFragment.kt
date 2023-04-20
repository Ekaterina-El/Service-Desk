package com.elka.servicedesk.view.ui.registrationScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elka.servicedesk.databinding.RegistrationFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.RegistrationViewModel

class RegistrationFragment: BaseFragment() {
  private lateinit var binding: RegistrationFragmentBinding
  private lateinit var viewModel: RegistrationViewModel

  private var fieldErrorsObserver = Observer<List<FieldError>> { errors ->
    showErrors(errors, fields)
  }

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_NEXT) {
      viewModel.clear()
//      navController.navigate(R.id.action_createOrganizationFragment_to_authFragment)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

    binding = RegistrationFragmentBinding.inflate(layoutInflater, container, false)
    binding.apply {
      lifecycleOwner = viewLifecycleOwner
      master = this@RegistrationFragment
      viewModel = this@RegistrationFragment.viewModel
    }

    return binding.root
  }

  override fun onResume() {
    super.onResume()
    viewModel.fieldErrors.observe(viewLifecycleOwner, fieldErrorsObserver)
    viewModel.error.observe(viewLifecycleOwner, errorObserver)
    viewModel.externalAction.observe(viewLifecycleOwner, externalActionObserver)
    viewModel.work.observe(viewLifecycleOwner, workObserver)
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.error.removeObserver(errorObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.work.removeObserver(workObserver)
  }

  fun goBack() {
    navController.popBackStack()
    viewModel.clear()
  }

  fun tryRegistration() {
    viewModel.tryRegistration()
  }


  private val fields by lazy {
    hashMapOf<Field, Any>(
      Pair(Field.FIRST_NAME, binding.layoutFirstName),
      Pair(Field.LAST_NAME, binding.layoutLastName),
      Pair(Field.PASSWORD, binding.layoutPassword),
      Pair(Field.PHONE_NUMBER, binding.layoutPhoneNumber),
      Pair(Field.EMAIL, binding.layoutEmail),
      Pair(Field.DIVISION, binding.errorDivision),
    )
  }
}