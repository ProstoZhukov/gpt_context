package ru.tensor.sbis.design.selection.ui.model.share

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta

/**
 * Реализация по умолчанию для модели данных заголовка.
 *
 * ao.zanin
 */
data class DefaultTitleItemModel(
    override val id: String,
    override val title: String,
) : TitleItemModel {

    override val subtitle: String? = null

    override lateinit var meta: SelectorItemMeta
}
