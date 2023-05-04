package com.elka.servicedesk.view.list.accidents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AccidentHeaderItemBinding
import com.elka.servicedesk.databinding.AccidentItemBinding
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.view.list.BaseAdapter

class AccidentsAdapter(val listener: AccidentItemViewHolder.Companion.Listener) :
  BaseAdapter<AccidentItem>() {

  private val accidentItems = mutableListOf<AccidentItemViewHolder>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return if (viewType == TYPE_HEADER) {
      val binding = AccidentHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      AccidentHeaderItemViewHolder(parent.context, binding)
    } else {
      val binding = AccidentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      val vh = AccidentItemViewHolder(parent.context, binding, listener)
      accidentItems.add(vh)
      vh
    }
  }

  override fun clear() {
    accidentItems.clear()
    super.clear()
  }

  fun updateItems() {
    try {
      accidentItems.forEach { it.update() }
    } catch (e: Exception) {

    }
  }

  override fun getItemViewType(position: Int): Int {
    return items[position].type
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    if (item.type == TYPE_HEADER) {
      (holder as AccidentHeaderItemViewHolder).bind(item.value as String)
    } else if (item.type == TYPE_ITEM) {
      (holder as AccidentItemViewHolder).bind(item.value as Accident)
    }
  }

  companion object {
    const val TYPE_HEADER = 1
    const val TYPE_ITEM = 2
  }
}