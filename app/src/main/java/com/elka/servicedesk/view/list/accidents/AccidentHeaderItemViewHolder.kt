package com.elka.servicedesk.view.list.accidents

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AccidentHeaderItemBinding


class AccidentHeaderItemViewHolder(
  val context: Context, val binding: AccidentHeaderItemBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(title: String) {
    binding.title.text = title
  }
}