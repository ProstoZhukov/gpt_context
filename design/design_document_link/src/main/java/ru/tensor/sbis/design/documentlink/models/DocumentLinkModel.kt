package ru.tensor.sbis.design.documentlink.models

import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Модель данных виджета документа-основания.
 *
 * Состоит из заголовка [title], комментария [comment], иконки [icon]
 * и флага отрисовки в одну строку [isSingleLineMode]
 *
 * @author da.zolotarev
 */
data class DocumentLinkModel(
    val title: String = "",
    val comment: String = "",
    val icon: SbisMobileIcon.Icon? = SbisMobileIcon.Icon.smi_Sabydoc,
    val isSingleLineMode: Boolean = false,
) {
    init {
        require(!(title.isEmpty() && comment.isEmpty())) { "At least one of this argument must be passed" }
    }
}
