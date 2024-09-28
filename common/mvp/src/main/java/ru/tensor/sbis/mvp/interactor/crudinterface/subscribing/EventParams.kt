package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

/**
 * Параметры для подписки на событие.
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class EventParams(
    /**
     * Название события.
     */
    val eventName: String,
    /**
     * Если true - подписка работает постоянно и не реагирует на вызовы pause.
     */
    val permanent: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventParams

        return eventName == other.eventName
    }

    override fun hashCode(): Int {
        return eventName.hashCode()
    }

}