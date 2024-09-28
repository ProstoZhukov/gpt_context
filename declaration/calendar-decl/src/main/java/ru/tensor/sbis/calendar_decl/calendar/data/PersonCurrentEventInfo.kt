package ru.tensor.sbis.calendar_decl.calendar.data

import android.text.Spannable
import android.text.SpannableString
import java.util.UUID

/**
 * Состояние плашки недоступности для текущего пользователя
 * @param eventTitle - текущий статус недоступности сотрудника, активен, если
 * сотрудник в отпуске/больничном/командировке
 * @param eventBackgroundColor - цвет подложки
 * @param docUuid - uuid документа ДРВ (отпуск, отгул и т.д.)
 * @param docType - тип документа ДРВ (отпуск, отгул и т.д.)
 */
data class PersonCurrentEventInfo(
    var eventTitle: Spannable = SpannableString(""),
    var eventBackgroundColor: Int = 0,
    val docUuid: UUID? = null,
    val docType: String? = null
)