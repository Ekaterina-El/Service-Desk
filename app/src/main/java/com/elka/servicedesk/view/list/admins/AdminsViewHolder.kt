package com.elka.servicedesk.view.list.admins

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AdminItemBinding
import com.elka.servicedesk.service.model.User

class AdminsViewHolder(
  val context: Context,
  val binding: AdminItemBinding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {
  private var admin: User? = null

  private val menu by lazy {
    val popupMenu = PopupMenu(context, binding.wrapper)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        else -> Unit
      }
      return@setOnMenuItemClickListener true
    }

    return@lazy popupMenu
  }

  fun bind(admin: User) {
    binding.admin = admin
    this.admin = admin

    binding.wrapper.setOnClickListener {
      listener.onSelect(admin)
    }

    binding.wrapper.setOnLongClickListener {
      menu.show()
      return@setOnLongClickListener true
    }
  }


  companion object {
    interface Listener {
      fun onSelect(admin: User)
    }
  }
}