package ru.tensor.sbis.design.design_menu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.HorizontalPosition.*
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.design_menu.model.ItemSelectionState.*

/**
 * Модель настроек элемента меню.
 *
 * @author ra.geraskin
 */
@Parcelize
data class MenuItemSettings(

    /** Цвет иконки. */
    val iconColor: SbisColor? = null,

    /** Цвет заголовка. */
    val titleColor: SbisColor? = null,

    /** Расположение иконки. */
    val iconAlignment: HorizontalPosition = LEFT,

    /** Состояние выбора элемента меню (работает если в самом меню включён режим выбора среди элементов). */
    val selectionState: ItemSelectionState = UNCHECKED,

    /** Должно ли отображаться пустое пространство на месте где должна быть иконка если иконка == null. */
    val emptyIconSpaceEnabled: Boolean = false

) : Parcelable {

    /**
     * Конструктор для совместимости со старой версией меню, где в API самого элемента использовался флаг
     * destructive: Boolean. Флаг отвечал за окраску заголовка и иконки в danger цвет.
     */
    constructor(
        isDestructive: Boolean,
        iconAlignment: HorizontalPosition = RIGHT,
        selectionState: ItemSelectionState = UNCHECKED,
        emptyIconSpaceEnabled: Boolean = false
    ) : this(
        iconColor = if (isDestructive) SbisColor.Attr(R.attr.dangerTextColor) else null,
        titleColor = if (isDestructive) SbisColor.Attr(R.attr.dangerTextColor) else null,
        iconAlignment = iconAlignment,
        selectionState = selectionState,
        emptyIconSpaceEnabled = emptyIconSpaceEnabled
    )

    /**
     * Конструктор для быстрой настройки selectionState поля, через аргумент isChecked:Boolean.
     */
    constructor(
        iconColor: SbisColor? = null,
        titleColor: SbisColor? = null,
        iconAlignment: HorizontalPosition = RIGHT,
        isChecked: Boolean,
        emptyIconSpaceEnabled: Boolean = false
    ) : this(
        iconColor = iconColor,
        titleColor = titleColor,
        iconAlignment = iconAlignment,
        selectionState = if (isChecked) CHECKED else UNCHECKED,
        emptyIconSpaceEnabled = emptyIconSpaceEnabled
    )

}