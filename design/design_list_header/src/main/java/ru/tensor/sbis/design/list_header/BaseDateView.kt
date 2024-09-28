package ru.tensor.sbis.design.list_header

import ru.tensor.sbis.design.list_header.format.FormattedDateTime

/**
 * Интерфейс, содержащий общую механику для форматирования текста в DateView и HeaderDateView
 *
 * @see HeaderDateView
 * @see ItemDateView
 *
 * @author ra.petrov
 */
internal interface BaseDateView {

    /**
     * Режим отображения даты и времени во view.
     * Позволяет определить для view, какой именно аспект данных FormattedDateTime
     * нужно отображать
     *
     * К примеру: и дата и время, и только дата
     *
     * @see FormattedDateTime
     */
    var dateViewMode: DateViewMode

    /**
     * Установка модели форматированных даты и времени FormattedDateTime
     *
     * @see FormattedDateTime
     */
    fun setFormattedDateTime(formattedDateTime: FormattedDateTime?)
}