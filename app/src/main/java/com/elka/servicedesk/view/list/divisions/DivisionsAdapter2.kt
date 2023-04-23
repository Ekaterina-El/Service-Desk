package com.elka.servicedesk.view.list.divisions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.DivisionItem2Binding
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.view.list.BaseAdapter

class DivisionsAdapter2(val listener: DivisionViewHolder2.Companion.Listener) :
  BaseAdapter<Division>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = DivisionItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
    return DivisionViewHolder2(parent.context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as DivisionViewHolder2).bind(item)
  }
}