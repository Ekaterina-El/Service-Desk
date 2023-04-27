package com.elka.servicedesk.view.list.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.AddImageItemBinding
import com.elka.servicedesk.databinding.ImageItemBinding
import com.elka.servicedesk.view.list.BaseAdapter


class ImagesAdapter(
  private val listenerItem: ImageItemViewHolder.Companion.Listener? = null,
  private val listenerAdd: ImageAddItemViewHolder.Companion.Listener? = null
) : BaseAdapter<ImageItem>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    return when (viewType) {
      TYPE_ADD_ITEM -> {
        val binding =
          AddImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ImageAddItemViewHolder(parent.context, binding, listenerAdd)
      }
      TYPE_ITEM -> {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ImageItemViewHolder(parent.context, binding, listenerItem)
      }
      else -> throw Exception("No image item")
    }
  }

  override fun getItemViewType(position: Int): Int {
    return items[position].type
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = items[position]
    when (item.type) {
      TYPE_ITEM -> (holder as ImageItemViewHolder).bind(item.value as String)
      TYPE_ADD_ITEM -> (holder as ImageAddItemViewHolder).bind()
    }
  }

  companion object {
    const val TYPE_ADD_ITEM = 1
    const val TYPE_ITEM = 2
  }
}