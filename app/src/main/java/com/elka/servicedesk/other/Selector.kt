package com.elka.servicedesk.other

import android.view.View
import android.widget.AdapterView

class Selector(val onSelect: (Any) -> Unit) : AdapterView.OnItemSelectedListener {
  override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    val selected = p0?.selectedItem ?: return
    onSelect(selected)
  }

  override fun onNothingSelected(p0: AdapterView<*>?) {}
}