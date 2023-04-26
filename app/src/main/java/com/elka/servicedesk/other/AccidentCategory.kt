package com.elka.servicedesk.other

enum class AccidentCategory(val text: String) {
  // Incidents
  NETWORKING_TECHNOLOGIES("Cетевые технологии"), PROTECTION_OF_INFORMATION_RESOURCES("Защита Информационных Ресурсов"), COMPUTER_SCIENCE(
    "Вычислительная техника"
  ),
  APPLICATION_SOFTWARE("Прикладное программное обеспечение"), DATABASE_OPERATION("Эксплуатация Баз данных"), CORPORATE_EMAIL(
    "Корпоративная электронная почта"
  ),
  PROVISION_OF_PRINTING_SERVICES_ON_A_PRINTER("Обеспечение услугами печати на принтере"), ACCESS_TO_EXTERNAL_INFORMATION_RESOURCES(
    "Доступ к внешним информационным ресурсам"
  ),
  PROVISION_OF_SERVICES_OF_INTELLIGENT_DISTRIBUTION_OF_INCOMING_CALLS("Обеспечение услугами интеллектуального распределения входящих вызовов"), TELECOMMUNICATIONS_EQUIPMENT(
    "Телекоммуникационное оборудование"
  ),
  DOCUMENT_MANAGEMENT("Документооборот"), CRM("CRM"), BILLING("Биллинг"), EXTERNAL_PORTAL("Портал внешний"), INTERNAL_PORTAL(
    "Портал внутренний"
  ),
  ERP("ERP"), HARDWARE("Аппаратное обеспечение"),

  // Requsets
  REPLACEMENT_OF_A_PERSONAL_COMPUTER("Замена персонального компьютера (ПК)"), UPGRADING_A_PC("Модернизация ПК"), WORKPLACE_RETROFIT(
    "Дооснащение рабочего места"
  ),
  REQUEST_FOR_REPAIR_OF_OFFICE_EQUIPMENT("Запрос на ремонт оргтехники"), SOFTWARE_INSTALLATION("Установка Программного обеспечения (ПО)"), REQUEST_FOR_SERVER_CAPACITY_ALLOCATION(
    "Запрос на выделение серверной мощности"
  ),
  REQUEST_FOR_CREATING_AN_ACCOUNT("Запрос на создание учетной записи"), REQUEST_FOR_GRANTING_RIGHTS_TO_THE_CORPORATE_INFORMATION_SYSTEM(
    "Запрос на предоставление прав к Корпоративной Информационной Системе (КИС)"
  ),
  REQUEST_FOR_EXTERNAL_WORK("Запрос на внешние работы"),
}