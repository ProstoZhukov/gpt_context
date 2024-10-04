package ru.tensor.sbis.design.selection.ui.factories

import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.CounterFormat
import javax.inject.Inject

/**
 * Реализация по умолчанию для [ItemMetaFactory]
 *
 * @author ma.kolpakov
 */
internal class ItemMetaFactoryImpl @Inject constructor(
    private val selectorItemHandleStrategy: SelectorItemHandleStrategy<SelectorItemModel>,
    private val counterFormat: CounterFormat,
    private val customisation: SelectorCustomisation
) : ItemMetaFactory {

    override fun attachItemMeta(model: SelectorItemModel) {
        model.meta = SelectorItemMeta(
            // TODO: 6/18/2020 https://online.sbis.ru/opendoc.html?guid=982a1cea-eb90-4cde-8f79-e0847239f673
            formattedCounter = if (model is RegionSelectorItemModel) counterFormat.format(model.counter) else null,
            handleStrategy = selectorItemHandleStrategy.onItemClick(model)
        )
        // в качестве типа могут выступать атрибуты из meta - нужно вызывать отдельно от инициализации
        model.meta.viewHolderType = customisation.getViewHolderType(model)
    }

    override fun attachSelectedItemMeta(model: SelectorItemModel) {
        attachItemMeta(model)
        model.meta.isSelected = true
    }
}