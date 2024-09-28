package ru.tensor.sbis.common_filters

import ru.tensor.sbis.common_filters.base.*

/**
 * Отмечаемый элемент списка фильтров с большим фото сотрудника, его именем и должностью
 *
 * @property name имя
 * @property post должность
 * @property photoUrl url фото
 */
data class CheckablePersonFilterItem(
    override val type: FilterType,
    override val uuid: String = type.id.toString(),
    val name: String,
    val post: String,
    val photoUrl: String,
    val clickableVm: ClickableVm = ClickableVm(),
    val checkableVm: CheckableVm = CheckableVm(),
    override val title: String = name
) : FilterItem, Checkable by checkableVm, Clickable by clickableVm {

    override fun onClick() {
        checkableVm.onClick()
        clickableVm.onClick()
    }

    fun onClick(itemClickHandler: FilterItemClickHandler<FilterItem>?) {
        onClick()
        itemClickHandler?.onItemClick(this)
    }
}