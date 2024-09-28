package ru.tensor.sbis.communication_decl.conversation_information

import android.os.Parcelable
import androidx.annotation.StringRes

/**
 * Интерфейс фильтров на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
interface ConversationInformationFilter : Parcelable {

    /** Ресурс подписи, для отображения её у чекбокса. */
    @get:StringRes
    val caption: Int

    /** Получить список всех доступных фильтров. */
    fun getAllFilters(): List<ConversationInformationFilter>
}