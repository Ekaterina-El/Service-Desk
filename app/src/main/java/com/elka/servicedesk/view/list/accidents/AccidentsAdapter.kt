package com.elka.servicedesk.view.list.accidents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AccidentItemBinding
import com.elka.servicedesk.service.model.Accident
import com.elka.servicedesk.view.list.BaseAdapter

class AccidentsAdapter(val listener: AccidentViewHolder.Companion.Listener) :
  BaseAdapter<Accident>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = AccidentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return AccidentViewHolder(parent.context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as AccidentViewHolder).bind(item)
  }
}