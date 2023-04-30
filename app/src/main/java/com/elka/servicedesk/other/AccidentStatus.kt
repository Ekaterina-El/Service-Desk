package com.elka.servicedesk.other

enum class AccidentStatus(val text: String) {
  ACTIVE("Создан"),
  IN_WORK("В работе"),
  FORWARD("Передан"),
  READY("Разрешен"),
  CLOSED("Закрыт инженером. Требуется подтверждение!"),
  WAITING("Ожидание"),
}