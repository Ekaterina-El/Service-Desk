package com.elka.servicedesk.view.list.users

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.R
import com.elka.servicedesk.databinding.ChipItemBinding
import com.elka.servicedesk.databinding.UserItemBinding
import com.elka.servicedesk.service.model.User
import com.google.android.material.chip.Chip

class UsersViewHolder(
  val context: Context, val binding: UserItemBinding, val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {
  private var user: User? = null

  private val menu by lazy {
    val popupMenu = PopupMenu(context, binding.wrapper)
    popupMenu.menu.add(0, BLOCK, 0, R.string.block)

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {
        BLOCK -> listener.onBlock(user!!)
        else -> Unit
      }
      return@setOnMenuItemClickListener true
    }

    return@lazy popupMenu
  }

  fun bind(user: User) {
    binding.user = user
    this.user = user

    if (user.divisionsId.isNotEmpty()) {
      showDivisions(user.divisionsLocal.map { it.name })
    }

    binding.wrapper.setOnClickListener {
      listener.onSelect(user)
    }

    binding.wrapper.setOnLongClickListener {
      menu.show()
      return@setOnLongClickListener true
    }
  }

  private fun showDivisions(divisions: List<String>) {
    val context = binding.root.context
    val layoutInflater = LayoutInflater.from(context)

    binding.divisionChips.removeAllViews()
    divisions.forEach {
      val chip = createChip(context, layoutInflater, it)
      binding.divisionChips.addView(chip)
    }
  }

  private fun createChip(context: Context, layoutInflater: LayoutInflater, value: String): Chip {
    val chip = ChipItemBinding.inflate(layoutInflater).root as Chip
    chip.text = value
    return chip
  }


  companion object {
    const val BLOCK = 1

    interface Listener {
      fun onSelect(admin: User)
      fun onBlock(admin: User)
    }
  }
}