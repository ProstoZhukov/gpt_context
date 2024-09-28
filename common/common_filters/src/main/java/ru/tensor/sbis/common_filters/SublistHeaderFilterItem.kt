package ru.tensor.sbis.common_filters

import ru.tensor.sbis.common_filters.base.*

/**
 * Заголовок блока в списке фильтров.
 * Опционально может быть сворачиваемым/разворачиваемым
 *
 * @property sublistType тип фильтров в блоке
 */
data class SublistHeaderFilterItem(
    override val type: FilterType,
    val sublistType: FilterType,
    override val uuid: String,
    override val title: String,
    val clickableVm: ClickableVm = ClickableVm(),
    val expandableVm: ExpandableVm = ExpandableVm()
) : FilterItem, Clickable by clickableVm, Expandable by expandableVm {

    override fun onClick() {
        expandableVm.onClick()
        clickableVm.onClick()
    }
}