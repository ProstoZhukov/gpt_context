package ru.tensor.sbis.design.selection.ui.factories

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Фабрика для создания [SelectorItemMeta] с автоматической доставкой статичной информации
 *
 * @author ma.kolpakov
 */
internal interface ItemMetaFactory {

    fun attachItemMeta(model: SelectorItemModel)

    fun attachSelectedItemMeta(model: SelectorItemModel)
}