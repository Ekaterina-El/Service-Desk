package com.elka.servicedesk.view.list.logs

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.LogsItemBinding
import com.elka.servicedesk.service.model.Log
import com.elka.servicedesk.service.model.User

class LogsViewHolder(
  val context: Context,
  val binding: LogsItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(log: Log) {
    binding.log = log
  }

}