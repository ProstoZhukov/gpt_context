package ru.tensor.sbis.toolbox_decl.linkopener.data

/**
 * Перечисление поддерживаемых типов ссылок на документы.
 *
 * СУЩЕСТВУЮЩИЕ НАИМЕНОВАНИЯ ТИПОВ ПЕРЕЧИСЛЕНИЙ НЕ ДОЛЖНЫ МЕНЯТЬСЯ:
 * 1. ПРИЧИНА, НАИМЕНОВАНИЯ ТИПОВ ИСПОЛЬЗУЮТСЯ ДЛЯ ФОРМИРОВАНИЯ ВНУТРЕННИХ СБИС ССЫЛОК sabylink://type.subtype/
 * 2. В СЛУЧАЕ ЯВНОЙ НЕОБХОДИМОСТИ ИЗМЕНЕНИЯ ДОЛЖНЫ БЫТЬ ПОДДЕРЖАНЫ В СООТВЕТСТВУЮЩИХ intent-filter МАНИФЕСТА :link_opener
 * 3. ПРИ ДОБАВЛЕНИИ НОВОГО ТИПА ДОКУМЕНТА, СМ. П.2 И README :link_opener
 *
 * @author as.chadov
 */
enum class DocType {
    /** Документ. */
    DOCUMENT,

    /** Письмо. */
    LETTER,

    /** Файл диска. */
    DISC,

    /** Наряд. */
    ORDER,

    /** Продажи. */
    TRADES,

    /**
     * Новости.
     * Пример: https://sabyget.ru/news/uuid
     */
    NEWS,

    /**
     * Новость для биллинга (биллинговое извещение).
     * Пример: https://reg.tensor.ru/news/uuid
     */
    BILLING_NEWS,

    /** Сокнет новости. */
    SOCNET_NEWS,

    /** Сокнет отчет. */
    SOCNET_NEWS_REPOST,

    /** Документ портала wi. */
    WI,

    /** Сокнет группа. */
    SOCNET_GROUP,

    /** Сокнет заголовок обсуждения. */
    GROUP_DISCUSSION_TOPIC,

    /** Сокнет вопрос обсуждения. */
    GROUP_DISCUSSION_QUESTION,

    /** Служебная записка задачи. */
    TASK_LETTER,

    /** Отчет фнс задачи. */
    TASK_FNS_REPORT,

    /** Вебинар. */
    WEBINAR,

    MEETING,
    MEETING_SERVICE,

    /**
     * Видео-совещание.
     * Пример: https://online.sbis.ru/videoconf/uuid
     */
    MEETING_VIDEOCALL,
    GROUP_SUGGESTIONS,

    /** Мероприятие. */
    EVENT,

    /** Страница профиля персоны. */
    PERSON,

    /** Трансляция вебинара. */
    WEBINAR_TRANSLATION,

    /**
     * Видео-звонок.
     * Пример: https://n.sbis.ru/call/room/uuid
     */
    VIDEOCALL,

    /**
     * Инструкция.
     * Пример: https://online.sbis.ru/instructdoc/
     */
    INSTRUCTDOC,

    /**
     * Контрактор (контрагент).
     * Пример: https://online.sbis.ru/contractors-mobile-api/wasaby/id
     */
    CONTRACTOR,

    /**
     * Документ базы знаний.
     * Пример: https://online.sbis.ru/knowledge-bases/uuid
     */
    KNOWLEDGE_BASE,

    /** Папка базы знаний.
     *  Пример: https://online.sbis.ru/knowledge-base/uuid?folderId=uuid
     */
    KNOWLEDGE_FOLDER,

    /**
     * Просмотр диалога (открытие диалога вне чате).
     * Пример: https://online.sbis.ru/open_dialog.html?guid=uuid
     */
    OPEN_DIALOG,

    /**
     * Просмотр чата (открытие диалога в чате).
     * Пример: https://online.sbis.ru/open_chat.html?guid=uuid
     */
    OPEN_CHAT,

    /** Курсы. */
    COURSES,

    /**
     * Приглашение на регистрацию.
     * Пример: https://online.sbis.ru/reg/invite/?request=uuid
     */
    REG_INVITE,

    /** Неизвестный документ/ссылка. */
    UNKNOWN,

    /** Ссылка для привязки устройства к точке продаж (/retail/point.device_link/) */
    RETAIL_POINT_DEVICE_LINK,

    /** region Типы ВНЕ микросервиса контроллера [LinkDecoratorService] */

    /** Неизвестный документ онлайна ("online.sbis.ru"). */
    UNKNOWN_ONLINE_DOC,

    /** Кандидат. */
    CANDIDATE,

    /** Вход по QR-коду (в sbisRetail и sbisWaiter). */
    QR_AUTH,

    /** Вход по QR-коду на веб (auth/toweb/?token)*/
    QR_AUTH_TO_WEB,

    /** Статья базы знаний. */
    ARTICLE,

    /** Заявка на получение НЭП. */
    APPLICATION_FOR_UNQUALIFIED_SIGNATURE,

    /** Установка пин-кода на сертификат. */
    SETTING_CERTIFICATE_PASSWORD,

    /** Карточка продажи розницы/салона */
    RETAIL_ORDER_CARD,

    /** Реестр записей салона */
    RETAIL_ORDER_SALON,

    /** Журнал записей салона */
    QUEUES_SCHEDULE,

    //region Saby Get
    /** Карточка компании. */
    SABYGET_COMPANY,

    /** Реферальная ссылка. */
    SABYGET_REFEREE,

    /** Сертификат. */
    SABYGET_CERTIFICATE,

    /** QR-код для заказа за столом. */
    SABYGET_QR_TABLE,

    /** QR-код для заказа за столом. */
    SABYGET_QR_PRESTO,

    /** Счет на оплату. */
    SABYGET_QR_FAST_PAYMENTS_SYSTEM,

    /** Штрихкод товара. */
    SABYGET_QR_MARKET,

    /** Штрихкод товара. */
    SABYGET_QR_EGAIS,

    /** Даныне покупки из чека. */
    SABYGET_QR_OFD_RECEIPT,

    /** Чек. */
    SABYGET_RECEIPT,

    /** Счет на оплату. */
    SABYGET_BILL,

    /** Главный экран. */
    SABYGET_MAIN,

    /** Подборка по городу. */
    SABYGET_CITY,

    /** Подборка по категории. */
    SABYGET_CATEGORY,

    /** Карточка номенклатуры. */
    SABYGET_NOMENCLATURE,

    /** Карточка новости Saby Get. */
    SABYGET_NEWS,

    /** Динамическая ссылка firebase. */
    SABYGET_PAGE_LINK,

    /** Не брендовая ссылка. */
    BRAND_UNKNOWN,

    /** Go-link ссылка Saby Get. */
    SABYGET_GO_LINK,
    //endregion

    /** endregion */
}
