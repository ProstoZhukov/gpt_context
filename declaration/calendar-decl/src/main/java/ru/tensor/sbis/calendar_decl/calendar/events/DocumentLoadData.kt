package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/** Параметры отображения документа в карточке событий */
@Parcelize
data class DocumentLoadData(
    /** UUID документа */
    val documentUuid: UUID?,
    /** Событие можно отредактировать */
    val eventCanEdit: Boolean,
    /** Событие из личного календаря */
    val isPersonalEvent: Boolean,
    /** Заголовок события (для случая ошибочной загрузки документа будет писать в названии документа) */
    val eventTitle: String?,
    /** Можно ли получить превью документа через модуль задач */
    val canGetPreview: Boolean
) : Parcelable