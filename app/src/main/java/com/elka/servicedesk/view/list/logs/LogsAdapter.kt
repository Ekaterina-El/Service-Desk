package com.elka.servicedesk.view.list.logs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.LogsItemBinding
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.BaseAdapter
import com.elka.servicedesk.view.list.admins.AdminsViewHolder

class LogsAdapter() : BaseAdapter<Log>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = LogsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return LogsViewHolder(parent.context, binding)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as LogsViewHolder).bind(item)
  }
}