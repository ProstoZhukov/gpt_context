package ru.tensor.sbis.common_filters

import androidx.databinding.ObservableInt
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import android.view.View
import ru.tensor.sbis.common_filters.base.*
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Отмечаемый элемент списка фильтров с возможностью задания содержимого слева от текста, а также счётчика справа
 *
 * @property icon ресурс текстовой иконки
 * @property drawable ресурс изображения
 * @property drawableTint цвет, в который требуется окрашивать [drawable]
 * @property imageUrl url изображения
 * @property imagePlaceholder заглушка, отображаемая до загрузки [imageUrl]
 * @property personData данные для отображения фото лица
 * @property count строка со значением счётчика
 */
open class CheckableFilterItem(
    override val type: FilterType,
    override val uuid: String = type.id.toString(),
    override val title: String = "",
    @StringRes
    val icon: Int = 0,
    @DrawableRes
    val drawable: Int = 0,
    @ColorInt
    val drawableTint: Int? = null,
    val imageUrl: String = "",
    @DrawableRes
    val imagePlaceholder: Int = 0,
    val personData: PhotoData? = null,
    val count: String = "",
    val clickableVm: ClickableVm = ClickableVm(),
    val checkableVm: CheckableVm = CheckableVm()
) : FilterItem, Checkable by checkableVm, Clickable by clickableVm {

    val singleCheckBoxVisibility = object : ObservableInt(checkableVm.checked) {
        override fun get() = when {
            checkStyle != CheckStyle.CHECKBOX_SINGLE -> View.GONE
            checkableVm.isChecked()                  -> View.VISIBLE
            count.isEmpty()                          -> View.INVISIBLE
            else                                     -> View.GONE
        }
    }

    override fun onClick() {
        checkableVm.onClick()
        clickableVm.onClick()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CheckableFilterItem

        if (type != other.type) return false
        if (uuid != other.uuid) return false
        if (title != other.title) return false
        if (icon != other.icon) return false
        if (drawable != other.drawable) return false
        if (drawableTint != other.drawableTint) return false
        if (imageUrl != other.imageUrl) return false
        if (imagePlaceholder != other.imagePlaceholder) return false
        if (personData != other.personData) return false
        if (count != other.count) return false
        if (clickableVm != other.clickableVm) return false
        if (checkableVm != other.checkableVm) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + drawable.hashCode()
        result = 31 * result + drawableTint.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + imagePlaceholder.hashCode()
        result = 31 * result + personData.hashCode()
        result = 31 * result + count.hashCode()
        result = 31 * result + clickableVm.hashCode()
        result = 31 * result + checkableVm.hashCode()
        return result
    }

    fun onClick(itemClickHandler: FilterItemClickHandler<FilterItem>?) {
        onClick()
        itemClickHandler?.onItemClick(this)
    }
}