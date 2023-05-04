package com.elka.servicedesk.view.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.elka.servicedesk.R
import com.elka.servicedesk.other.AccidentStatus
import com.elka.servicedesk.service.model.Accident

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

@BindingAdapter("app:escalationTime")
fun setStatusColor(textView: TextView, accident: Accident?) {
	if (accident == null) {
		textView.text = ""
		return
	}

	val textColorRes = if (accident.executionTime == null) R.color.attention else R.color.on_primary
	textView.setTextColor(textView.context.getColor(textColorRes))
	textView.text = accident.executionTimeS
}