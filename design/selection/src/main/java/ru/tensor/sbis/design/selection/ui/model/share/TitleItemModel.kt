package ru.tensor.sbis.design.selection.ui.model.share

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Модель данных заголовка
 *
 * @author ma.kolpakov
 */
interface TitleItemModel : SelectorItemModel {

    /**
     * Подзаголовок не применяется для ячеек заголовков. Всегда `null`
     */
    override val subtitle: String?
}
