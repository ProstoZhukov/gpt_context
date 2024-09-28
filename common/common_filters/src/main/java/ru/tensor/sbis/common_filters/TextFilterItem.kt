package ru.tensor.sbis.common_filters

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import ru.tensor.sbis.common_filters.base.Clickable
import ru.tensor.sbis.common_filters.base.ClickableVm
import ru.tensor.sbis.common_filters.base.FilterItem
import ru.tensor.sbis.common_filters.base.FilterType

/**
 * Элемент списка фильтров с возможностью отображения названия выбираемой опции справа
 *
 * @property label текст выбранной опции
 */
class TextFilterItem(
    override val type: FilterType,
    override val uuid: String = type.id.toString(),
    override val title: String,
    label: String = "",
    val clickableVm: ClickableVm = ClickableVm()
) : FilterItem, Clickable by clickableVm {

    val label = ObservableField<String>(label)

    val hasLabel = object : ObservableBoolean(this.label) {
        override fun get() = label.isNotEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextFilterItem

        if (type != other.type) return false
        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (clickableVm != other.clickableVm) return false
        if (label.get() != other.label.get()) return false
        if (hasLabel != other.hasLabel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + clickableVm.hashCode()
        result = 31 * result + label.get().hashCode()
        result = 31 * result + hasLabel.hashCode()
        return result
    }
}