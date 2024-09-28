package ru.tensor.sbis.communicator.common.util.header_date

import ru.tensor.sbis.design.list_header.format.FormattedDateTime

/**
 * View holder с датой.
 * Необходим для точечных обновлений через payload.
 *
 * @author vv.chekurda
 */
interface DateViewHolder {

    /**
     * Обновить view с датой
     *
     * @param formattedDateTime модель даты и времени
     */
    fun setFormattedDateTime(formattedDateTime: FormattedDateTime)

    companion object {

        const val CHANGE_DATE_PAYLOAD = "change_date_visibility_payload"
    }
}