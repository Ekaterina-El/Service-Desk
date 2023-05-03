package com.elka.servicedesk.view.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.elka.servicedesk.R
import com.elka.servicedesk.other.AccidentStatus

@BindingAdapter("app:statusColor")
fun setStatusColor(textView: TextView, status: AccidentStatus?) {
	val colorRes = when(status) {
		AccidentStatus.IN_WORK -> R.color.in_work

		AccidentStatus.CLOSED,
		AccidentStatus.WAITING,
		AccidentStatus.ESCALATION -> R.color.attention

		AccidentStatus.READY -> R.color.ready

		else -> R.color.on_primary
	}
	textView.setTextColor(textView.context.getColor(colorRes))
}