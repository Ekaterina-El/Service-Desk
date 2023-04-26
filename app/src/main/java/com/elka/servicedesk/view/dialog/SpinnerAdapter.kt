package com.elka.servicedesk.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.elka.servicedesk.databinding.SpinnerItemBinding
import com.elka.servicedesk.other.SpinnerItem

class SpinnerAdapter(context: Context, val items: List<SpinnerItem>) :
  ArrayAdapter<SpinnerItem>(context, 0, items) {
  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

    val binding: SpinnerItemBinding = when (convertView) {
      null -> SpinnerItemBinding.inflate(layoutInflater)
      else -> convertView.tag as SpinnerItemBinding
    }

    binding.text = items[position].text
    binding.root.tag = binding
    return binding.root
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val binding: SpinnerItemBinding = when (convertView) {
      null -> SpinnerItemBinding.inflate(layoutInflater)
      else -> convertView.tag as SpinnerItemBinding
    }

    binding.root.tag = binding
    getItem(position)?.let { binding.text = it.text }

    return binding.root
  }
}