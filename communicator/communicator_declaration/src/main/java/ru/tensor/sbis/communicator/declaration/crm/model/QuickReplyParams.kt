package ru.tensor.sbis.communicator.declaration.crm.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.util.UUID

/**
 * Модель параметров для настройки отображаемого списка быстрых ответов.
 *
 * @property channelUUID uuid чата, для которого открываем быстрые ответы.
 * @property needSearchInput нужно ли отображать строку поиска.
 * @property needFolderView нужно ли отображать вью текущей папки.
 * @property resultKey ключ, по которому будем возвращать текст выбранного ответа.
 *
 * @author dv.baranov
 */
@Parcelize
class QuickReplyParams(
    val channelUUID: UUID? = null,
    val needSearchInput: Boolean = true,
    val needFolderView: Boolean = true,
    val resultKey: String = StringUtils.EMPTY,
) : Serializable, Parcelable {

    /**
     * Признак того, что поиск осуществляется по набору в поле ввода сообщения.
     */
    @IgnoredOnParcel
    val isEditSearch = !needSearchInput && !needFolderView
}
