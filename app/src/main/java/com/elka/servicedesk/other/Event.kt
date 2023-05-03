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
  CLOSE_ACCIDENT_BY_ENGINEER("Отправлена заявка на подтверждении разрешения проблемы"),
	CLOSE_ACCIDENT_BY_USER("Заявка закрыта"),
	USER_ADDED_MORE_INFO("Пользователь предоставил дополнительную информацию"),
  ENGINEER_REQUEST_TO_ADD_MORE_INFO("Инженер запросил дополнительную информацию"),
	SENT_ESCALATION("Отправлен запрос на передачу другому инженеру"),
	CHANGED_ENGINEER("Заменен инжинер"),
}