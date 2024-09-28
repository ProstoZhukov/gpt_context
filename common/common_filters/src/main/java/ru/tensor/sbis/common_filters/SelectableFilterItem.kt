package ru.tensor.sbis.common_filters

import androidx.annotation.DrawableRes
import ru.tensor.sbis.common_filters.base.*
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData

/**
 * Выбираемый элемент списка фильтров с возможностью отображения стрелки или счётчика справа, а также изображения слева
 *
 * @property imageUrl url изображения
 * @property imagePlaceholder заглушка изображения при загрузке
 * @property personViewData данные фото сотрудника
 * @property hasArrow нужно ли отображать стрелку справа
 * @property count строка со значением счётчика
 */
data class SelectableFilterItem(
    override val type: FilterType,
    override val uuid: String = type.id.toString(),
    override val title: String = "",
    val imageUrl: String = "",
    @DrawableRes
    val imagePlaceholder: Int = 0,
    val personViewData: SbisPersonViewData? = null,
    val hasArrow: Boolean = false,
    val count: String = "",
    val clickableVm: ClickableVm = ClickableVm(),
    val selectableVm: SelectableVm = SelectableVm()
) : FilterItem, Selectable by selectableVm, Clickable by clickableVm {

    override fun onClick() {
        selectableVm.onClick()
        clickableVm.onClick()
    }

    fun onClick(itemClickHandler: FilterItemClickHandler<FilterItem>?) {
        onClick()
        itemClickHandler?.onItemClick(this)
    }
}