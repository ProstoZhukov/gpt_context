package ru.tensor.sbis.common_filters

import androidx.databinding.ObservableInt
import ru.tensor.sbis.common_filters.base.Clickable
import ru.tensor.sbis.common_filters.base.ClickableVm
import ru.tensor.sbis.common_filters.base.FilterItem
import ru.tensor.sbis.common_filters.base.FilterType

/**
 * Элемент списка фильтров "Ещё".
 * Используется при наличии возможности отображения ещё некоторого числа элементов
 *
 * @property moreCount число элементов, доступных для отображения
 */
class MoreFilterItem(
    override val type: FilterType,
    override val uuid: String = type.id.toString(),
    val clickableVm: ClickableVm = ClickableVm(),
    more: Int = 0
) : FilterItem, Clickable by clickableVm {

    val moreCount = ObservableInt(more)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MoreFilterItem

        if (type != other.type) return false
        if (uuid != other.uuid) return false
        if (clickableVm != other.clickableVm) return false
        if (moreCount.get() != other.moreCount.get()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + clickableVm.hashCode()
        result = 31 * result + moreCount.get().hashCode()
        return result
    }
}