package com.elka.servicedesk.other

enum class AccidentStatus(val text: String) {
  ACTIVE("Активен"),
  IN_WORK("В работе"),
  FORWARD("Передан"),
  READY("Разрешен"),
  CLOSED("Закрыт"),
  WAITING("Ожидание"),
}