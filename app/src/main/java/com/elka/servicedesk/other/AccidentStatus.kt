package com.elka.servicedesk.other

enum class AccidentStatus(val text: String) {
  ACTIVE("Активен"),
  IN_WORK("В работе"),
  FORWARD("Передан"),
  READY("Разрешен"),
  WAIN_ACCEPT_FROM_USER("Ожидается подтверждение от пользователе о разрешении пробелемы"),
  CLOSED("Закрыт"),
  WAITING("Ожидание"),
}