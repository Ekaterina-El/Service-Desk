package com.elka.servicedesk.other

enum class Event(val text: String) {
  ADDED_ENGINEER("Добавлен инженер"),
  BLOCKED_ENGINEER("Заблокирован инженер"),

  ADDED_ADMIN("Добавлен администратор"),
  BLOCKED_ADMIN("Заблокирован администратор"),

  REGISTERED_USER("Зарегистрировался пользователь"),

  UPDATE_USER("Отредактированы данные пользователя"),
  UPDATE_ENGINEER("Отредактированы данные инженера"),
  UPDATE_ADMIN("Отредактированы данные админа"),
  UPDATE_MANAGER("Отредактированы данные менаджера"),

  ADDED_DIVISION("Добавлено подразделение"),
  REMOVED_DIVISION("Удалено подразделение"),
  CHANGED_DIVISION("Изменено подразделение сотрудника"),
  REMOVED_EMPLOYER("Удален сотрудник из подразделения"),
  ADDED_EMPLOYER("Добавлен сотрудник в подразделение"),
  ADDED_INCIDENT("Добавлен инцидент"),
  ADDED_REQUEST("Добавлен запрос"),
	ACCEPT_INCIDENT_TO_WORK("Инцидент принят в работу"),
	ACCEPT_REQUEST_TO_WORK("Запрос принят в работу"),
}