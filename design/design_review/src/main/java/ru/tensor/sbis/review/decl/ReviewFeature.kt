package ru.tensor.sbis.review.decl

import androidx.lifecycle.LiveData
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.review.ReviewEvent
import ru.tensor.sbis.review.action.ReviewState
import ru.tensor.sbis.review.triggers.Trigger

/**
 * Контракт сервиса оценок в приложении
 *
 * @author ma.kolpakov
 */
interface ReviewFeature : Feature {
    /**
     * Подписка на изменение состояния ReviewFlow
     * @see ReviewState
     */
    val reviewState: LiveData<ReviewState>

    /**
     * Установить правила для предложения оценить приложение
     */
    @Deprecated("удалить после https://online.sbis.ru/opendoc.html?guid=774306ca-fe30-4ba8-927c-083c13fcea13")
    fun registerTrigger(trigger: Trigger)

    /**
     * Установить правила для предложения оценить приложение
     */
    @Deprecated("удалить после https://online.sbis.ru/opendoc.html?guid=774306ca-fe30-4ba8-927c-083c13fcea13")
    fun unRegisterTrigger(trigger: Trigger)

    /**
     * Опубликовать событие [eventEnum] с задержкой [delay] в систему оценок
     */
    fun <T> onEvent(eventEnum: T, delay: Long = 0) where T : ReviewEvent, T : Enum<*>

    /**
     * TODO: 06/18/2022 [Метод удалить после перехода на новую версию](https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa)
     * Опубликовать событие [event] с задержкой [delay] в систему оценок
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("onEventReview")
    @Deprecated(
        "https://online.sbis.ru/opendoc.html?guid=d8571409-ec5e-49f8-8c34-e3506a295daa",
        ReplaceWith("Используй версию метода принимающую ReviewEvent")
    )
    fun onEvent(event: Enum<*>, delay: Long = 0)
}