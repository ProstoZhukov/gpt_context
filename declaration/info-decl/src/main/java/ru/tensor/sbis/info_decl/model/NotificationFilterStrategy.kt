package ru.tensor.sbis.info_decl.model

/**
 * Стратегия формирования фильтра уведомлений.
 *
 * @author ev.grigoreva
 */
sealed class NotificationFilterStrategy(private val notificationTypes: Array<out NotificationType>) {

    /**
     * Стратегия включения типов в пустой фильтр
     */
    class IncludeFilterStrategy(vararg types: NotificationType) : NotificationFilterStrategy(types)

    /**
     * Стратегия исключения типов из фильтра с полным перечнем типов
     */
    class ExcludeFilterStrategy(vararg types: NotificationType) : NotificationFilterStrategy(types)

    /**
     * Получить типы уведомлений для обработки по указанной стратегии в виде набора числовых значений
     */
    fun getNotificationTypes(): HashSet<Int> {
        val types = HashSet<Int>(notificationTypes.size)
        for (type in notificationTypes) {
            types.add(type.value)
        }
        return types
    }

    /**
     * Получить типы уведомлений для обработки по указанной стратегии в виде набора моделей типа
     */
    fun getNotificationTypesModelSet(): Set<NotificationType> {
        return notificationTypes.toSet()
    }

    /**
     * Соотнести указанный тип уведомлений со списком типов, указанных в конфигурации
     * @param[type] тип уведомления
     * @return true - указанный тип уведомления необходимо обрабатывать
     */
    fun require(type: NotificationType): Boolean {
        return when (this) {
            is IncludeFilterStrategy -> {
                containsType(type)
            }
            is ExcludeFilterStrategy -> {
                !containsType(type)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationFilterStrategy

        if (!notificationTypes.contentEquals(other.notificationTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        return notificationTypes.contentHashCode()
    }

    private fun containsType(notificationType: NotificationType): Boolean {
        return notificationTypes.contains(notificationType)
    }

}