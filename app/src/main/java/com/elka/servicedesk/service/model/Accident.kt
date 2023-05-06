package com.elka.servicedesk.service.model

import com.elka.servicedesk.other.*
import com.elka.servicedesk.view.list.accidents.AccidentItem
import com.elka.servicedesk.view.list.accidents.AccidentsAdapter
import java.util.*

data class Accident(
	var type: AccidentType = AccidentType.INCIDENT,
	var id: String = "",

	var subject: String = "",
	var message: String = "",

	var urgency: UrgencyCategory = UrgencyCategory.LOW,
	var category: AccidentCategory = AccidentCategory.NETWORKING_TECHNOLOGIES,

	var photosURL: List<String> = listOf(),

	var divisionId: String = "",
	var divisionLocal: Division? = null,

	var userId: String = "",
	var userLocal: User? = null,

	var engineerId: String? = null,
	var engineerLocal: User? = null,

	var moreInfo: List<AccidentMoreInfo> = listOf(),

	// 	reasonOfEscalation*
	var reasonOfExcalation: String = "",
	// 	senderOfEscalation*
	var senderOfExcalation: User? = null,

	var createdDate: Date = Constants.getCurrentDate(),
	var pickUpTime: Date? = null,
	var status: AccidentStatus = AccidentStatus.ACTIVE,

	) : java.io.Serializable {
	var executionTime: Date?
		get() {
			val currentTime = Constants.getCurrentDate()

			val timeOfEnd = createdDate.time + urgency.hours * 3600 * 1000
			val timeLeft = timeOfEnd - currentTime.time
			return if (timeLeft > 0) Date(timeLeft) else null // check is deadline burned
		}
		set(v) {}

	var executionTimeS: String
		get() = executionTime?.timeLeft()?.let { "На выполнение осталось: $it" } ?: "Сроки пропущены"
		set(v) {}

	// senderOfEscalation*
	var senderOfExcalationS: String
		get() = "${senderOfExcalation?.fullName} (${senderOfExcalation?.role?.text})"
		set(v) {}

	var pickUpTimeS: String
		get() = pickUpTime?.toLogFormat() ?: ""
		set(v) {}

	var createdDateS: String
		get() = createdDate.toLogFormat()
		set(v) {}

	var info: String
		get() = "${urgency.text} срочность / ${divisionLocal?.name} / ${userLocal?.fullName} / $createdDateS"
		set(v) {}
}

fun List<Accident>.filterBy(s: String) = this.filter {
	it.subject.contains(s, true) || it.message.contains(s, true) || it.urgency.text.contains(
		s, true
	) || it.category.text.contains(s, true) || it.divisionLocal?.name?.contains(
		s, true
	) == true || it.userLocal?.fullName?.contains(s, true) == true || it.createdDateS.contains(
		s, true
	) || it.status.text.contains(s, true)
}

fun List<Accident>.splitAndSortByStatus(): List<Accident> {
	val waitingRequests =
		this.filter { it.status == AccidentStatus.WAITING }.sortedByDescending { it.createdDate }
	val activeRequests =
		this.filter { it.status == AccidentStatus.ACTIVE }.sortedByDescending { it.createdDate }
	val inWorkRequests =
		this.filter { it.status == AccidentStatus.IN_WORK }.sortedByDescending { it.createdDate }
	val escalationRequests =
		this.filter { it.status == AccidentStatus.ESCALATION }.sortedByDescending { it.createdDate }
	val closedRequests =
		this.filter { it.status == AccidentStatus.CLOSED }.sortedByDescending { it.createdDate }
	val readyRequests =
		this.filter { it.status == AccidentStatus.READY }.sortedByDescending { it.createdDate }

	val accidents = mutableListOf<Accident>()
	accidents.addAll(waitingRequests)
	accidents.addAll(activeRequests)
	accidents.addAll(inWorkRequests)
	accidents.addAll(escalationRequests)
	accidents.addAll(closedRequests)
	accidents.addAll(readyRequests)

	return accidents
}

fun List<Accident>.splitAndSort(): MutableList<Accident> {
	val items = mutableListOf<Accident>()

	val incidentsList = this.filter { it.type == AccidentType.INCIDENT }.splitAndSortByStatus()
	val requestsList = this.filter { it.type == AccidentType.REQUEST }.splitAndSortByStatus()
	items.addAll(incidentsList)
	items.addAll(requestsList)

	return items
}


fun List<Accident>.toAccidentItems() = this.map { AccidentItem(AccidentsAdapter.TYPE_ITEM, it) }

fun List<Accident>.accidentsByDivisions(): List<AccidentItem> {
	val items = mutableListOf<AccidentItem>()

	this.groupBy { it.divisionLocal?.name ?: "" }.forEach {
		// add header
		val headerName = if (it.key == "") "Другие" else it.key
		items.add(AccidentItem(type = AccidentsAdapter.TYPE_HEADER, value = headerName))

		val divisionAccidents = it.value.splitAndSortByStatus().toAccidentItems()
		items.addAll(divisionAccidents)
	}

	return items
}

fun List<Accident>.allToAccidentItems(): List<AccidentItem> {
	val items = mutableListOf<AccidentItem>()
	val incidents =
		this.filter { it.type == AccidentType.INCIDENT }.sortedByDescending { it.createdDate }
	val requests =
		this.filter { it.type == AccidentType.REQUEST }.sortedByDescending { it.createdDate }

	if (incidents.isNotEmpty()) {
		items.add(
			AccidentItem(
				type = AccidentsAdapter.TYPE_HEADER, value = AccidentType.INCIDENT.text
			)
		)

		items.addAll(incidents.toAccidentItems())
	}

	if (requests.isNotEmpty()) {
		items.add(
			AccidentItem(
				type = AccidentsAdapter.TYPE_HEADER, value = AccidentType.REQUEST.text
			)
		)

		items.addAll(requests.toAccidentItems())
	}

	return items
}