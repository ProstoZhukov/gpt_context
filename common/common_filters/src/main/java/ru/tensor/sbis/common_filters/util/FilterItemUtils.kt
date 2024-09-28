package ru.tensor.sbis.common_filters.util

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.tensor.sbis.common_filters.CheckableFilterItem
import ru.tensor.sbis.common_filters.SelectableFilterItem
import ru.tensor.sbis.common_filters.base.CheckStyle
import ru.tensor.sbis.common_filters.base.CheckableVm
import ru.tensor.sbis.common_filters.base.ClickableVm
import ru.tensor.sbis.common_filters.base.FilterType
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Содержимое слева от текста в отмечаемом элементе списка фильтров
 */
sealed class CheckableItemLeftContent {

    /**
     * Не отображать ничего слева от текста
     */
    object None : CheckableItemLeftContent()

    /**
     * Текстовая иконка
     */
    class TextIcon(
        @StringRes
        val icon: Int
    ) : CheckableItemLeftContent()

    /***
     * Drawable изображение
     */
    class DrawableIcon(
        @DrawableRes
        val drawable: Int,
        @ColorInt
        val tintColor: Int
    ) : CheckableItemLeftContent()

    /**
     * Изображение по url
     */
    class Image(
        val url: String,
        @DrawableRes
        val placeholder: Int
    ) : CheckableItemLeftContent()

    /**
     * Фото лица
     */
    class Person(val personData: PhotoData, val post: String = "") : CheckableItemLeftContent()
}

/**
 * Содержимое слева от текста в выбираемом элементе списка фильтров
 */
sealed class SelectableItemLeftContent {

    /**
     * Не отображать ничего слева от текста
     */
    object None : SelectableItemLeftContent()

    /**
     * Изображение по url
     */
    class Image(
        val url: String,
        @DrawableRes
        val placeholder: Int
    ) : SelectableItemLeftContent()

    /**
     * Фото лица
     */
    class Person(val personViewData: SbisPersonViewData) : SelectableItemLeftContent()
}

/**
 * Метод для упрощённого создания [CheckableFilterItem] с возможностью указания одного из вариантов содержимого слева
 * от текста
 */
fun createCheckableFilterItem(
    type: FilterType,
    uuid: String = type.id.toString(),
    title: String = "",
    checkStyle: CheckStyle = CheckStyle.NONE,
    leftContent: CheckableItemLeftContent = CheckableItemLeftContent.None,
    count: String = "",
    isChecked: Boolean = false,
    onClickAction: Runnable? = null
): CheckableFilterItem {
    return CheckableFilterItem(
        type,
        uuid,
        title,
        (leftContent as? CheckableItemLeftContent.TextIcon)?.icon ?: 0,
        (leftContent as? CheckableItemLeftContent.DrawableIcon)?.drawable ?: 0,
        (leftContent as? CheckableItemLeftContent.DrawableIcon)?.tintColor ?: 0,
        (leftContent as? CheckableItemLeftContent.Image)?.url.orEmpty(),
        (leftContent as? CheckableItemLeftContent.Image)?.placeholder ?: 0,
        (leftContent as? CheckableItemLeftContent.Person)?.personData,
        count,
        ClickableVm(onClickAction),
        CheckableVm(checkStyle, isChecked)
    )
}

/**
 * Метод для упрощённого создания [SelectableFilterItem] с возможностью указания одного из вариантов содержимого слева
 * от текста
 */
fun createSelectableFilterItem(
    type: FilterType,
    uuid: String = type.id.toString(),
    title: String = "",
    leftContent: SelectableItemLeftContent = SelectableItemLeftContent.None,
    hasArrow: Boolean = false,
    count: String = "",
    onClickAction: Runnable? = null
): SelectableFilterItem {
    return SelectableFilterItem(
        type,
        uuid,
        title,
        (leftContent as? SelectableItemLeftContent.Image)?.url.orEmpty(),
        (leftContent as? SelectableItemLeftContent.Image)?.placeholder ?: 0,
        (leftContent as? SelectableItemLeftContent.Person)?.personViewData,
        hasArrow,
        count,
        ClickableVm(onClickAction)
    )
}