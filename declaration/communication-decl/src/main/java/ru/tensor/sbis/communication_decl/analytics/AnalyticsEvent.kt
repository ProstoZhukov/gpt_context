package ru.tensor.sbis.communication_decl.analytics

import android.os.Bundle
import java.io.Serializable

/**
 * Интерфейс события аналитики.
 *
 * @author dv.baranov
 */
interface AnalyticsEvent : Serializable {

    /**
     * Функционал (Имя модуля, из которого вызывается событие).
     */
    val functional: String

    /**
     * Контекст (Название экрана или flow, в котором находится пользователь).
     */
    val analyticContext: String

    /**
     * Событие по которому отправляем аналитику.
     */
    val event: String

    /**
     * Дополнительные параметры для события аналитики.
     */
    val bundle: Bundle
        get() = Bundle()
}
