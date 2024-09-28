package ru.tensor.sbis.communicator.common.analytics

/**
 * Тип фильтра реестра диалогов для аналитики.
 */
internal enum class DialogsAnalyticFilter(val filterName: String) {
    /** Все без какой-либо дополнительной фильтрации */
    ALL("all"),

    /** Диалоги с входящими сообщениями */
    INCOMING("incoming"),

    /** Диалоги с непрочитанными сообщениями */
    UNREAD("unread"),

    /** Диалоги с исходящими сообщениями */
    UNANSWERED("unanswered"),

    /** Диалоги для сообщений архива */
    DELETED("deleted"),
}

/**
 * Тип фильтра реестра каналов для аналитики.
 */
internal enum class ChatsAnalyticFilter(val filterName: String) {
    /** Все без какой-либо дополнительной фильтрации */
    ALL("all"),

    /** Чаты с непрочитанными сообщениями */
    UNREAD("unread"),

    /** Удаленные чаты */
    HIDDEN("deleted"),
}

/**
 * Тип фильтра реестра контактов для аналитики.
 */
internal enum class ContactsAnalyticFilter(val filterName: String) {
    /** По дате последнего сообщения */
    BY_LAST_MESSAGE_DATE("date"),

    /** По имени */
    BY_NAME("fio"),
}

/**
 * Тип реестра при переключении вкладок.
 */
internal enum class ReeAnalytic(val reeName: String) {
    /** Диалоги */
    DIALOGS("dialogs"),

    /** Каналы */
    CHATS("chats"),
}

/**
 * Куда производится переназначение консультации.
 */
enum class ReassignConsultationTarget(val value: String) {
    /** В канал */
    CHANNEL("В канал"),

    /** В линию */
    LINE("В линию"),

    /** Обратно в очередь */
    QUEUE("Обратно в очередь"),

    /** Оператору */
    OPERATOR("Оператору"),
}
