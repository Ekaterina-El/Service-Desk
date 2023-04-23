package com.elka.servicedesk.view.list.divisions

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.DivisionItem2Binding
import com.elka.servicedesk.databinding.DivisionItemBinding
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.service.model.Division
import com.elka.servicedesk.service.model.User
import com.elka.servicedesk.view.list.users.UsersViewHolder

class DivisionViewHolder2(
  val context: Context,
  val binding: DivisionItem2Binding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {
  private var division: Division? = null

  private val menu by lazy {
    val popupMenu = PopupMenu(context, binding.wrapper)
    popupMenu.menu.add(0, DELETE, 0, R.string.delete)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        DELETE -> listener.onDelete(division!!)
        else -> Unit
      }
      return@setOnMenuItemClickListener true
    }

    return@lazy popupMenu
  }


  fun bind(division: Division) {
    binding.division = division
    this.division = division

    binding.wrapper.setOnLongClickListener {
      menu.show()
      return@setOnLongClickListener true
    }
  }

  companion object {
    const val DELETE = 1

    interface Listener {
      fun onDelete(division: Division)
    }
  }
}