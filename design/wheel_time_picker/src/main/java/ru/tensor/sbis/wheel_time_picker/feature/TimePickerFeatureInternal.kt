package ru.tensor.sbis.wheel_time_picker.feature

import io.reactivex.subjects.BehaviorSubject
import org.joda.time.LocalDateTime
import ru.tensor.sbis.common.rx.RxContainer

/**
 * Служебный контракт фичи выбора даты и времени.
 *
 * @author us.bessonov
 */
internal interface TimePickerFeatureInternal : TimePickerFeature {

    /**
     * Предназначен для публикации результатов изменения выбора начального значения даты и времени.
     */
    val startTimeSubject: BehaviorSubject<RxContainer<LocalDateTime>>

    /**
     * Предназначен для публикации результатов изменения выбора конечного значения даты и времени.
     */
    val endTimeSubject: BehaviorSubject<RxContainer<LocalDateTime>>

    /**
     * Опубликовать подтверждённые результаты выбора даты и времени.
     */
    fun publishResult()
}