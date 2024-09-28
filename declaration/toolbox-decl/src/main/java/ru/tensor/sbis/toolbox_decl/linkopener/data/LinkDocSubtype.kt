package ru.tensor.sbis.toolbox_decl.linkopener.data

/**
 * Перечисление подтипов ссылок, используется, например для сервисов Disk и Tasks.
 *
 * СУЩЕСТВУЮЩИЕ НАИМЕНОВАНИЯ ПОДТИПОВ ПЕРЕЧИСЛЕНИЙ НЕ ДОЛЖНЫ МЕНЯТЬСЯ:
 * 1. ПРИЧИНА, НАИМЕНОВАНИЯ ПОДТИПОВ ИСПОЛЬЗУЮТСЯ ДЛЯ ФОРМИРОВАНИЯ ВНУТРЕННИХ СБИС ССЫЛОК sabylink://type.subtype/
 * 2. В СЛУЧАЕ ЯВНОЙ НЕОБХОДИМОСТИ ИЗМЕНЕНИЯ ДОЛЖНЫ БЫТЬ ПОДДЕРЖАНЫ В СООТВЕТСТВУЮЩИХ intent-filter МАНИФЕСТА :link_opener
 * 3. ПРИ ДОБАВЛЕНИИ НОВОГО ПОДТИПА ДОКУМЕНТА, СМ. П.2 И README :link_opener
 *
 * @author as.chadov
 */
enum class LinkDocSubtype {
    UNKNOWN,
    DISK_FILE,
    DISK_XML,
    DISK_IMAGE,
    DISK_XLS,
    DISK_DOC,
    DISK_PPT,
    DISK_PDF,
    DISK_TXT,
    DISK_ARCHIVE,
    DISK_AUDIO,
    DISK_VIDEO,
    DISK_FOLDER,
    DISK_URL,
    DISK_LINK,
    DISK_SABYDOC,
    TASK_NOTE,
    TASK_WORK_PLAN,
    TASK_WORK_PLAN_ITEM,
    TASK_CAPITAL_DOC,
    TASK_SERTIFICATE,
    TASK_FUNDS_MOVE,
    TASK_PREPAYMENT_REPORT,
    TASK_LEAD_LIST,
    TASK_ACCREDITATION,
    TASK_OUTCOME,
    TASK_INFRACTION,
    TASK_RECLAMATION,
    TASK_VACATION,
    TASK_ORDER,
    TASK_INVOICE,
    TASK_INCENTIVE,
    TASK_PENALTY,
    REVIEW_ITEM,
    RECLAMATION,

    HIRE,
    CHANGE_RATE,
    DISMISSAL,
    TRANSFER,

    /** Командировка. */
    BUSINESS_TRIP,

    /** Отгул. */
    TIME_OFF,

    /** Переработка. */
    OVERTIME_HOURS,

    /** Прогул. */
    TRUANCY,

    /** Простой. */
    DOWNTIME,

    /** Пункт чеклиста. */
    CHECKLIST_ITEM,

    /** Заявка на доставку. */
    TASK_DELIVERY_TASK,

    /** Путевой лист. */
    TASK_WAYBILL,

    /** Приходный ордер для [DocType.DOCUMENT]. */
    ORDER_INCOME,

    /** Расходный ордер для [DocType.DOCUMENT]. */
    ORDER_EXPEND,

    /** Исходящий платёж для [DocType.DOCUMENT]. */
    OUTGOING_PAYMENT,

    /** Входящий платёж для [DocType.DOCUMENT]. */
    INCOMING_PAYMENT,

    /** Заявка на оплату для [DocType.DOCUMENT]. */
    PAYMENT_REQUEST,
    DAY_TIMESHEET,

    /** Документ-требование. */
    TASK_REQUIREMENTS,

    /** Проект. */
    TASK_PROJECT,

    /** Заправка. */
    REFUELING,

    /** Отчет ФНС. */
    TASK_FNS_REPORT,

    /** Поступление. */
    ADMISSION,

    /** Реализация. */
    SELLING,

    /** Договор. */
    AGREEMENT,

    /** Согласование цен. */
    PRICE_MATCHING_IN,

    /** Совещание. **/
    MEETING,

    /** Видео-совещание. **/
    MEETING_VIDEOCALL,

    /** Вебинар. **/
    WEBINAR,

    /** Мероприятие. **/
    EVENT,

    /** Инструкция. **/
    INSTRUCTION,

    /** Список клиентов. */
    CLIENTS_LIST,

    /** Грфик смен. */
    SHIFT_SCHEDULE
}
