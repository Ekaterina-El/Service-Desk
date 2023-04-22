package com.elka.servicedesk.view.list.admins

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AdminItemBinding
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.BaseAdapter

class AdminsAdapter(val listener: AdminsViewHolder.Companion.Listener) : BaseAdapter<User>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = AdminItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return AdminsViewHolder(parent.context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as AdminsViewHolder).bind(item)
  }
}