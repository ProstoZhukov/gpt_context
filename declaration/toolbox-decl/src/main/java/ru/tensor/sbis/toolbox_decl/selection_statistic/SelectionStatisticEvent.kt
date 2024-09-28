package ru.tensor.sbis.toolbox_decl.selection_statistic

/**
 * Событие статистики выбора адресата.
 *
 * @property context контекст события.
 * @property action  действие события.
 *
 * @author dv.baranov
 */
class SelectionStatisticEvent(val context: String, val action: String) {

    /**
     * Функционал события.
     */
    val functional = SELECTION_FUNCTIONAL
}

/**
 * Кейсы статистики выбора адресатов.
 */
enum class SelectionStatisticUseCase(val value: String) {

    UNKNOWN("Неизвестный кейс выбора адресата"),

    REPORT_SELECT_ORGANIZATION("Выбор организации для отчета в календаре"),

    FACES_CONTRACTORS_SELECTION("Выбор контрагентов при документообороте"),

    CERTIFICATE_REGISTRATION("Выбор организации при регистрации сертификатов"),

    REPOST_NEWS_TO_GROUP("Выбор группы при репосте новости"),

    BUSINESS_TRIP_CASE("Выбор адреса командировки"),

    CALENDAR_DAY_CASE("Выбор клиента для работы на выезде"),

    TASK_CREATION("Выбор контрагента в создании задачи"),

    CITY_CHOOSER("Выбор города"),

    PAYMENT_DRAFT("Выбор контрагента в черновике платежа"),

    MONEY_ROOT("Выбор контрагента для создания исходящего документа в разделе *Деньги*"),

    DEALS_DRAFT("Выбор клиентав в черновике сделки"),

    DEALS_FILTER("Выбор клиента для фильтра списка сделок"),

    DEALS_EXECUTOR("Выбор исполнителя при создании сдалки"),

    CRM_CHAT_FILTER("Выбор клиента для фильтра чатов crm"),

    INOUT_FILTER("Выбор клиентов для фильтра входящих/исходящих документов"),

    INOUT_DRAFT("Выбор клиента в черновике для входящих/исходящих документов"),

    INOUT_CREATE("Выбор клиента для входящих/исходящих документов при создании"),

    DOCUMENTS_FILTER("Выбор клиентов для фильтрации списка документов"),

    WRH_DOC_CREATE("Выбор клиента при создании складского документа"),

    WRH_DOC_ORGANISATION("Выбор грузоотправителя/грузополучателя для складского документа"),

    WRH_DOC_COUNTER_PARTY("Выбрать контрагента для складского документа."),

    CITY_SELECTOR("Выбор города"),

    REGION_SELECTOR("Выбор региона"),

    RECORD_CARD_CLIENT("Выбор клиента в карточке записи"),
}

/**
 * Действие в событии выбора адресата.
 */
enum class SelectionStatisticAction(val value: String) {

    /** Событие открытия выбора адресата. */
    OPEN_SELECTION("Открытие"),

    /** Событие поиска в выборе адресата. */
    SEARCH("Поиск"),

    /** Событие нажатия на плюс в множественном выборе адресата. */
    CLICK_ON_ADD_BUTTON("Выбор по плюсу"),

    /** Событие нажатия строки в списке выбора адресата. */
    CLICK_ON_ITEM("Выбор по строке"),
}

private const val SELECTION_FUNCTIONAL = "Компонент выбора"
