package com.elka.servicedesk.other

enum class AccidentStatus(val text: String) {
  ACTIVE("Создан"),
  IN_WORK("В работе"),
  ESCALATION("Передается другому инженеру"),
  READY("Закрыт"),
  CLOSED("Закрыт инженером. Требуется подтверждение!"),
  WAITING("Ожидается дополнительная информация от пользователя"),
}