package ru.tensor.sbis.business.common.ui.base

/**
 * Представляет семейство пользовательских ошибок и исключений приложения Бизнес
 *
 * @author as.chadov
 *
 * @property message сообщение об ошибке
 */
sealed class Error(
    override val message: String = "",
    override val cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Ошибка уведомление, сообщающая о пустом результате запроса данных.
     * Не свидетельствует о проблемах при работе с источником данных, например не получен
     * следующий разворот поскольку данных больше нет
     */
    class NoDataReceivedError(override val message: String = "") : Error(message)

    /**
     * Ошибка, сообщающая об отсутствии данных за выбранный период
     */
    class NoDataForPeriodError : Error()

    /**
     * Ошибка получения данных по поисковому запросу
     */
    class NoSearchDataError : Error()

    /**
     * Ошибка, сообщающая об отсутствии данных для установленного фильтра
     */
    class NoDataForFilterError : Error()

    /**
     * Неизвестная ошибка
     */
    class UnknownError(
        override val message: String = "",
        override val cause: Throwable? = null
    ) : Error(message, cause)

    /**
     * Ошибка, отсутствия прав доступа, разрешений
     */
    class NoPermissionsError(override var message: String = "") : Error(message)

    /**
     * Ошибка получения данных, вызванная проблемой соединения с сетью
     */
    class NetworkError(override val message: String = "") : Error(message)

    /**
     * Ошибка, вызванная отсутствие интернет соединения
     */
    class NoInternetConnection : Error()

    /**
     * Исключение, получаемое например при обращении к кэшу до синхронизации с облаком
     */
    class NotLoadYetError : Error()
}