package com.elka.servicedesk.view.list.moreInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.MoreInfoItemBinding
import com.elka.servicedesk.other.AccidentMoreInfo
import com.elka.servicedesk.view.list.BaseAdapter

class MoreInfoAdapter : BaseAdapter<AccidentMoreInfo>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val binding = MoreInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MoreInfoViewHolder(parent.context, binding)
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = items[position]
		(holder as MoreInfoViewHolder).bind(item)
	}
}