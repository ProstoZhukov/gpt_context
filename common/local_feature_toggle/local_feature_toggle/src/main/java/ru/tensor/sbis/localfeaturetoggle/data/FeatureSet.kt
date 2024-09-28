package ru.tensor.sbis.localfeaturetoggle.data

/**
 * Список локальных фич.
 *
 * @author mb.kruglova
 */
enum class FeatureSet(val id: String, val description: String) {
    CONTROLLER_NAVIGATION("controllerNavigation", "Модули с онлайна"),
    CONTRACT_SELECTOR("contractSelector", "Выбор контактов в календаре"),
    NEW_VACATION_TYPES("newVacationTypes", "Выбор новых типов отпусков"),
    VACATION_REGULATIONS("calendarVacationRegulations", "Выбор регламента для отпуска"),
    PDF_SABYDOC_PREVIEWER("pdfSabydocPreviewer", "PDF сабидок в просмотрщике"),
    NAVIGATION_FILTER("navigationFilter", "Фильтр доступных пунктов навигации"),
    ATTACHMENTS_UPLOAD_ERROR("attachmentsUploadError", "Ошибки загрузки вложений"),
    NEW_MOTIVATION("newMotivation", "Новая мотивация"),
    NEW_RETAIL_REPORTS("newRetailReports", "Новые отчеты продаж"),
    MENTIONS("mentions", "Упоминания в сообщениях"),
    CRUD_DEALS("crudDeals", "Реестр сделок на CRUD3"),
    NEW_EVENTS_REGISTRY("newEventsRegistry", "Новый реестр встреч"),
    FILES_TASKS_DIALOG("filesTasksDialog", "Файлы и задачи в диалоге"),
    NEW_CONTRACTORS("newContractors", "Новый реестр контрагентов"),
    COURIER_NEW_ADDRESS("courier_new_address", "Новый компонент ввода адреса."),
    MAIN_PAGE("main_page", "Главная страница"),
    CADRES_DOCS_EDIT_VACATION("cadresDocsEditVacation", "Редактирование отпуска в документах"),

    /** todo удалить https://online.sbis.ru/opendoc.html?guid=08351a04-a478-4975-a306-3d892cd32302&client=3 */
    NEW_MOTIVATION_SCREENS("new_motivation_screens", "Новые экраны мотивации")
}