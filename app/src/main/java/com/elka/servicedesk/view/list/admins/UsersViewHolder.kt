package com.elka.servicedesk.view.list.admins

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.AdminItemBinding
import com.elka.servicedesk.service.model.User

class UsersViewHolder(
  val context: Context,
  val binding: AdminItemBinding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {
  private var admin: User? = null

  private val menu by lazy {
    val popupMenu = PopupMenu(context, binding.wrapper)
    popupMenu.menu.add(0, BLOCK, 0, R.string.block)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        BLOCK -> listener.onBlock(admin!!)
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
    const val BLOCK = 1
    interface Listener {
      fun onSelect(admin: User)
      fun onBlock(admin: User)
    }
  }
}