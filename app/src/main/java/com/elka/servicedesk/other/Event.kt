package com.elka.servicedesk.other

enum class Event(val text: String) {
  ADDED_ANALYST("Добавлен аналитик"),
  BLOCKED_ANALYST("Заблокирован аналитик"),

  ADDED_ADMIN("Добавлен администратор"),
  BLOCKED_ADMIN("Заблокирован администратор"),

  REGISTERED_USER("Зарегистрировался пользователь"),

  UPDATE_USER("Отредактированы данные пользователя"),
  UPDATE_ANALYST("Отредактированы данные аналитика"),
  UPDATE_ADMIN("Отредактированы данные админа"),
  UPDATE_MANAGER("Отредактированы данные менаджера"),

  ADDED_DIVISION("Добавлено подразделение"),
  REMOVED_DIVISION("Удалено подразделение"),
}