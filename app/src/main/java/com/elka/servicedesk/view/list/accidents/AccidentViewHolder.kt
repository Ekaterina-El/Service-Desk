package com.elka.servicedesk.view.list.accidents

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AccidentItemBinding
import com.elka.servicedesk.service.model.Accident

class AccidentViewHolder(
  val context: Context,
  val binding: AccidentItemBinding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(accident: Accident) {
    binding.accident = accident

    binding.wrapper.setOnClickListener {
      listener.onSelect(accident)
    }
  }

  companion object {
    interface Listener {
      fun onSelect(accident: Accident)
    }
  }
}