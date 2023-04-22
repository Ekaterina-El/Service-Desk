package com.elka.servicedesk.view.list.divisions

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.DivisionItemBinding
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User

class DivisionViewHolder(
  val context: Context,
  val binding: DivisionItemBinding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(division: Division) {
    binding.division = division

    binding.button.setOnClickListener {
      listener.onDelete(division)
    }
  }

  companion object {
    interface Listener {
      fun onDelete(division: Division)
    }
  }
}