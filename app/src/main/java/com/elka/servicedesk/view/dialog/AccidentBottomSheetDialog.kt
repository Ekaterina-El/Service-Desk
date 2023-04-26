package com.elka.servicedesk.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elka.servicedesk.databinding.AccidentsBottomSheetDialogBinding
import com.elka.servicedesk.other.AccidentType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccidentBottomSheetDialog(private val itemListener: ItemClickListener) : BottomSheetDialogFragment(){
  private lateinit var binding: AccidentsBottomSheetDialogBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = AccidentsBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.addRequest.setOnClickListener { itemListener.onItemClick(AccidentType.REQUEST) }
    binding.addIncident.setOnClickListener { itemListener.onItemClick(AccidentType.INCIDENT) }

  }
  companion object {
    interface ItemClickListener {
      fun onItemClick(accidentType: AccidentType)
    }
  }
}