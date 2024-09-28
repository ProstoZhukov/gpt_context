package ru.tensor.sbis.pin_code.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Класс с вариантами длительности действия пин-кода.
 * @property duration Время действия пин-кода в минутах. Отрицательное значение используется для специфичного времени
 * использования.
 *
 * @author mb.kruglova
 */
sealed class PinCodePeriod(val duration: Long) : Parcelable {

    /**
     * Период действия - до конца сессии.
     *
     * @author mb.kruglova
     */
    @Parcelize
    class Session : PinCodePeriod(-1L)

    /**
     * Период действия - 15 минут.
     *
     * @author mb.kruglova
     */
    @Parcelize
    class QuarterHour : PinCodePeriod(15L)

    /**
     * Период действия - 30 минут.
     *
     * @author mb.kruglova
     */
    @Parcelize
    class HalfHour : PinCodePeriod(30L)

    /**
     * Период действия - один час.
     *
     * @author mb.kruglova
     */
    @Parcelize
    class Hour : PinCodePeriod(60L)
}