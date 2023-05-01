package com.elka.servicedesk.view.list.moreInfo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.elka.servicedesk.databinding.MoreInfoItemBinding
import com.elka.servicedesk.other.AccidentMoreInfo

class MoreInfoViewHolder(
	val context: Context,
	val binding: MoreInfoItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

	fun bind(moreInfo: AccidentMoreInfo) {
		binding.moreInfo = moreInfo
	}
}