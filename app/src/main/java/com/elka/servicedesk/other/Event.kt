package com.elka.servicedesk.other

enum class Event(val text: String) {
  ADDED_ANALYST("Добавлен аналитик"),
  BLOCKED_ANALYST("Заблокирован аналитик"),

  ADDED_ADMIN("Добавлен администратор"),
  BLOCKED_ADMIN("Заблокирован администратор"),

  REGISTERED_USER("Зарегистрировался пользователь"),
}