package ru.tensor.sbis.common.navigation

import ru.tensor.sbis.toolbox_decl.navigation.DefaultNavxIdResolver
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Перечень идентификаторов используемых в приложениях разделов навигации.
 * При изменении следует обеспечивать сортировку в алфавитном порядке.
 *
 * @param identifiers Строковые идентификаторы, соответствующие разделу приложения в структуре навигации.
 * Возможность переопределения предоставлена временно, пока не будет повсеместно применено размещение элемента вкладкой,
 * либо элементом верхнего уровня навигации. Значение допустимо переопределять только до инициализации контроллера.
 *
 * @author us.bessonov
 */
enum class NavxId(vararg identifiers: String) : NavxIdDecl {
    APPROVE("docs-in-work"), // Согласовать
    BONUSES("bonuses"),
    CALENDAR("calendar") { // Календарь
        override fun matches(id: String, parentId: String?): Boolean = matches(id) && parentId == null
    },
    CALENDAR_ACTIVITY("calendar_activity"), // Активность (календарь)
    CALENDAR_MAIN("calendar") { // Календарь (главная)
        override fun matches(id: String, parentId: String?): Boolean = matches(id) && parentId != null
    },
    CALENDAR_MY_DOCS("regevents", "regevents-mobile"), // Мои документы (календарь)
    CALENDAR_SCHEDULE("work-schedule", "work-schedule-new"), // Графики работ (календарь)
    CALL_HISTORY("calls"), // Звонки
    CASHBOX("retail-shift"), // Касса
    CATALOG("nomenclature-catalog"), // Каталог
    CHATS("chats"), // Каналы
    CLAIM_CHATS("claim-chats"), // Чаты
    CLIENTS("crm-clients"), // Клиенты
    COMMUNICATOR("communicator"), // Сообщения
    COMPANY_LIST("company-list"), // Компании
    CONTACTS("contacts"), // Контакты
    COOK_ROOMS("kitchen"), // Кухня
    MAIN_PAGE("main-page"), // Главная страница (курьер)
    COURIER_WORKS("transport-waybills"), // Работы (курьер)
    CRM_DOCS("outflows"),   // Раздел документы в CRM
    DEALS("crm-deals"), // Сделки
    DEBTS("retail-debts"), // Долги
    DIALOGS("dialogs"), // Диалоги
    DISCOUNT_CARDS("cards"),
    DISK("disk"), // Диск
    DISK_BACKGROUND_CALL("diskCallbackgrounds"), // Записи видеозвонков
    DISK_BUFFER("diskBuffer"), // Буфер
    DISK_COMMON("diskCommon"), // Диск компании
    DISK_DELETED("diskTrash"), // Удалённые
    DISK_FROM_MESSAGES("diskMessages"), // Из сообщений
    DISK_MY("diskMy"), // Мой диск
    DISK_SCREENSHOTS("diskScreenshots"), // Скриншоты
    DISK_SHARED_WITH_ME("diskShared"), // Со мной поделились
    DISK_SIGNED_BY_ME("diskSigned"), // Я подписал
    DISK_VIDEO_CALLS("diskVideocalls"), // Записи видеозвонков
    EMPLOYEES("staff"), // Сотрудники (раздел)
    FAVORITES_WIKI("favoritesWiki"), // База знаний (избранные)
    INSTRUCTIONS("my-instructions"), // Инструкции
    MEETINGS("meetings"), // Встречи
    MONEY("money"), // Деньги
    MOTIVATION_DASHBOARD("my-motivation-dashboard"), // Мотивация новая
    MOTIVATION("my_motivation", "motivation-my-mobile"), // Мотивация
    MOTIVATION_INCENTIVE("motivation-my-incentives"), // Стимулы
    MOTIVATION_PAY("motivationPay"), // Выплаты
    MOTIVATION_SALARY("motivation-my-salary"), // Зарплата
    MY_DOCUMENTS("my_statements"), // Документы
    NEWS("news-feed"), // Новости
    NOTIFICATIONS("notifications"), // Уведомления
    PRESTO_BOOKINGS("presto-bookings"), // Бронь
    PRESTO_CASHBOX("presto-shift"), // Касса (Presto)
    PRESTO_ORDERS("presto-orders"), // Заказы
    PRESTO_REPORTS("presto-reports"), // Отчёты Presto
    PROMOCODES("promocodes"),
    PURCHASES("orders"),
    QUEUES_SCHEDULE("queues-schedule"), // Журнал (раздел Saby Clients)
    REPORTS_CALENDAR("ereport_calendar"), // Календарь отчётности
    REQUIREMENTS("Requirements"), // Требования
    RESTAURANTS("presto-restaurants"), // Заказы (официант), корневой раздел
    RETAIL_CATALOG("retail-catalog"), // Каталог (розница, официант)
    RETAIL_CLIENTS("retail-clients"), // Клиенты (Розница)
    RETAIL_ORDER_SALON("record-list"), // Записи (раздел Saby Clients)
    RETAIL_REPORTS("retail-reports"), // Отчеты Розница (Магазин)
    REVIEWS("feedback"),
    SABYGET_CHATS("contacts"),
    SABYGET_COMPANIES("city"),
    SABYGET_FAVORITES("favorites"),
    SABYGET_NEWS("newsline"),
    SABYGET_PROFILE("profile"),
    SALES("ofd-report"), // Продажи
    SALE_REGISTER_V2("sale_register_v2"), // Продажа
    SETTINGS("my-general-info"), // Настройки
    STAFF("staff-list", "staff-list-new"), // Сотрудники
    STOP_LIST("stop-list"), // Стоп-лист
    SUPPORT("support"), // Служба поддержки
    SUPPORT_CLAIMS("support-service_claims"), // Обращения
    TASKS("work"), // Задачи (совмещённый)
    TASKS_FROM_ME("tasks-from-me"), // Задачи от меня
    TASKS_ON_ME("tasks-in-work"), // Задачи (на мне)
    TIPS("saby-tips"), // Чаевые
    TRANSPORT_MAP("transport-tracking"), // Карта
    WAITER_BOOKING("waiterBooking"), // Бронь (официант)
    WAITER_ORDERS("waiterOrders"), // Заказы (официант)
    WAREHOUSE_DOCS("warehouse-docs"), // Складские документы
    WIKI("knowledge-base"); // База знаний

    /**
     * Строковые идентификаторы, соответствующие одной и той же структурной единице навигации.
     */
    override var ids = identifiers.toSet()

    override var forceEnabled = false

    @Deprecated(
        "Строковых идентификаторов может быть несколько. Будет удалено по " +
            "https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
        replaceWith = ReplaceWith("ids")
    )
    override var id: String
        get() = ids.first()
        set(value) {
            ids = setOf(value)
        }

    /**
     * Проверить соответствие строкового идентификатора данному значению.
     */
    override fun matches(id: String) = ids.contains(id)

    /**
     * Проверить соответствие строкового идентификатора данному значению, учитывая id родительского элемента.
     * Применимо, в частности, чтобы различать элементы с одинаковым строковым id на разном уровне иерархии.
     */
    open fun matches(id: String, parentId: String?) = matches(id)

    companion object {
        /**
         * Получить значение [NavxId], соответствующее строковому идентификатору.
         */
        fun of(id: String) = values().find { it.matches(id) }

        /**
         * Получить значение [NavxId], соответствующее строковому идентификатору, с дополнительным условием совпадения
         * родительского идентификатора.
         */
        fun of(id: String, parentId: String?) = values().find { it.matches(id, parentId) }

        init {
            DefaultNavxIdResolver.resolver = { NavxId.of(it) }
        }
    }
}