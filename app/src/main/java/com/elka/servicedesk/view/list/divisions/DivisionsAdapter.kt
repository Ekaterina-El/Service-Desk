package com.elka.servicedesk.view.list.divisions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.elka.servicedesk.databinding.SpinnerDivisionItemBinding
import com.elka.servicedesk.service.model.Division

class DivisionsAdapter(context: Context, divisions: List<Division>) :
  ArrayAdapter<Division>(context, 0, divisions) {
  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  override fun getView(division: Int, convertView: View?, parent: ViewGroup): View {

    val binding: SpinnerDivisionItemBinding = when (convertView) {
      null -> SpinnerDivisionItemBinding.inflate(layoutInflater)
      else -> convertView.tag as SpinnerDivisionItemBinding
    }

    binding.division = getItem(division)
    binding.root.tag = binding
    return binding.root
  }

  override fun getDropDownView(division: Int, convertView: View?, parent: ViewGroup): View {
    val binding: SpinnerDivisionItemBinding = when (convertView) {
      null -> SpinnerDivisionItemBinding.inflate(layoutInflater)
      else -> convertView.tag as SpinnerDivisionItemBinding
    }

    binding.root.tag = binding
    getItem(division)?.let { binding.division = it }

    return binding.root
  }
}