package com.elka.servicedesk.other

enum class UrgencyCategory(val text: String, val hours: Int) {
  LOW("Низкая", 36),  MEDIUM("Средняя", 12), HIGH("Высокая", 1)
}
fun getUrgencyCategoriesSpinnerItems() = UrgencyCategory.values().map {
  return@map SpinnerItem(it.text, it)
}