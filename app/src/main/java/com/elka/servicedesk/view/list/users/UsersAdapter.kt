package com.elka.servicedesk.view.list.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.UserItemBinding
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.BaseAdapter

class UsersAdapter(val listener: UsersViewHolder.Companion.Listener) : BaseAdapter<User>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return UsersViewHolder(parent.context, binding, listener)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    (holder as UsersViewHolder).bind(item)
  }
}