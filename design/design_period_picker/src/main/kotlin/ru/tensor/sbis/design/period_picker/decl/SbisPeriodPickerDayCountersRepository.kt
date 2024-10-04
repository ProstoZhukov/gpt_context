package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

/**
 * Репозиторий, предоставляющий счётчики по дням.
 *
 * @author mb.kruglova
 */
interface SbisPeriodPickerDayCountersRepository {

    /**
     * Получить подписку на счётчики дня.
     * @param range диапазон дат, по которому нужно получать счётчики.
     * @return Flow, излучающий набор соответствий счётчика с датой дня.
     */
    fun getDayCountersFlow(range: ClosedRange<Calendar>): Flow<Map<Calendar, Int>>

    /**
     * Фабрика для создания [SbisPeriodPickerDayCountersRepository] репозитория, предоставляющего счётчики по дням.
     */
    interface Factory : Parcelable {
        /**
         * Создать репозиторий, предоставляющий счётчики по дням.
         */
        fun createSbisPeriodPickerDayCountersRepository(): SbisPeriodPickerDayCountersRepository
    }
}