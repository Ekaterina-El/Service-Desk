package com.elka.servicedesk.view.list.images

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AddImageItemBinding
import com.elka.servicedesk.databinding.ImageItemBinding

class ImageItemViewHolder(
  val context: Context,
  val binding: ImageItemBinding,
  val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(url: String) {
    binding.url = url

    binding.wrapper.setOnClickListener {
      listener.onRemove(url)
    }
  }

  companion object {
    interface Listener {
      fun onRemove(url: String)
    }
  }
}