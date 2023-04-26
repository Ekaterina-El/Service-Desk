package com.elka.servicedesk.other

enum class UrgencyCategory(val text: String) {
  HIGH("Высокая"), MEDIUM("Средняя"), LOW("Низкая"),
}
fun getUrgencyCategoriesSpinnerItems() = UrgencyCategory.values().map {
  return@map SpinnerItem(it.text, it)
}