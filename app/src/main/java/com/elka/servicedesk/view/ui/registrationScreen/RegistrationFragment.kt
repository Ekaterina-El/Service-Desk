package com.elka.servicedesk.view.ui.registrationScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.RegistrationFragmentBinding
import com.elka.servicedesk.other.Action
import com.elka.servicedesk.other.Field
import com.elka.servicedesk.other.FieldError
import com.elka.servicedesk.other.Selector
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.list.divisions.DivisionsAdapter
import com.elka.servicedesk.view.ui.BaseFragment
import com.elka.servicedesk.viewModel.DivisionsViewModel
import com.elka.servicedesk.viewModel.RegistrationViewModel

class RegistrationFragment : BaseFragment() {
  private lateinit var binding: RegistrationFragmentBinding
  private lateinit var viewModel: RegistrationViewModel

  private val divisionsViewModel by activityViewModels<DivisionsViewModel>()


  private var fieldErrorsObserver = Observer<List<FieldError>> { errors ->
    showErrors(errors, fields)
  }

  private val divisionsObserver = Observer<List<Division>> {
    val spinnerAdapter = DivisionsAdapter(requireContext(), it)
    binding.divisionSpinner.adapter = spinnerAdapter
  }

  private val externalActionObserver = Observer<Action?> {
    if (it == Action.GO_NEXT) {
      viewModel.clear()
      navController.navigate(R.id.action_registrationFragment_to_authFragment)
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
    divisionsViewModel.divisions.observe(viewLifecycleOwner, divisionsObserver)

    binding.divisionSpinner.onItemSelectedListener = divisionSpinnerListener
  }

  override fun onStop() {
    super.onStop()

    divisionsViewModel.divisions.removeObserver(divisionsObserver)
    viewModel.fieldErrors.removeObserver(fieldErrorsObserver)
    viewModel.error.removeObserver(errorObserver)
    viewModel.externalAction.removeObserver(externalActionObserver)
    viewModel.work.removeObserver(workObserver)

    binding.divisionSpinner.onItemSelectedListener = null
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
      Pair(Field.EMAIL, binding.layoutEmail),
      Pair(Field.DIVISION, binding.errorDivision),
    )
  }

  private val divisionSpinnerListener by lazy {
    Selector { viewModel.setDivision(it as Division) }
  }
}