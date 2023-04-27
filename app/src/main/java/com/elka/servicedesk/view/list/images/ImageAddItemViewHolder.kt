package com.elka.servicedesk.view.list.images

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AddImageItemBinding

class ImageAddItemViewHolder(
  val context: Context,
  val binding: AddImageItemBinding,
  val listener: Listener?
) : RecyclerView.ViewHolder(binding.root) {

  fun bind() {
    binding.wrapper.setOnClickListener {
      listener?.onSelect()
    }
  }

  companion object {
    interface Listener {
      fun onSelect()
    }
  }
}