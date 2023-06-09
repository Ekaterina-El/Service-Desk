package com.elka.servicedesk.view.list

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
  protected open val items = mutableListOf<T>()
  fun getAllItems() = items

  override fun getItemCount() = items.size

  open fun setItems(newItems: List<T>) {
    clear()
    newItems.forEach { setItem(it) }
  }

  fun addItem(user: T, position: Int? = null) {
    val pos = if (position == null || position < 0) items.size else position
    items.add(pos, user)
    notifyItemInserted(pos)
  }

  fun removeItem(item: T) {
    val idx = items.indexOf(item)
    if (idx == -1) return

    items.removeAt(idx)
    notifyItemRemoved(idx)
  }

  private fun setItem(item: T) {
    items.add(item)
    notifyItemInserted(items.size)
  }

  open fun clear() {
    notifyItemRangeRemoved(0, items.size)
    items.clear()
  }

  fun removeByPos(pos: Int) {
    items.removeAt(pos)
    notifyItemRemoved(pos)
  }

}