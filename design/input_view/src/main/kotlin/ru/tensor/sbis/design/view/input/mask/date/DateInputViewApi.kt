package ru.tensor.sbis.design.view.input.mask.date

import ru.tensor.sbis.design.view.input.mask.api.BaseMaskInputViewApi
import java.util.Calendar
import java.util.Date

/**
 * Api поля ввода с маской для ввода даты [DateInputView].
 *
 * @author mb.kruglova
 */
interface DateInputViewApi : BaseMaskInputViewApi {

    /** Минимальное значение вводимой даты. */
    var minDate: Date?

    /** Максимальное значение вводимой даты. */
    var maxDate: Date?

    /** Обновить значение даты. */
    fun updateValue(calendar: Calendar)

    /** Получить значение как дата. */
    fun getDate(): Calendar?
}