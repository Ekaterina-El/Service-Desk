package com.elka.servicedesk.view.list.divisions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AdminItemBinding
import com.elka.servicedesk.databinding.DivisionItemBinding
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.BaseAdapter

class DivisionsDinamicAdapter(val listener: DivisionViewHolder.Companion.Listener) : BaseAdapter<Division>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = DivisionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return DivisionViewHolder(parent.context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as DivisionViewHolder).bind(item)
  }
}