package ru.tensor.sbis.design.design_menu.viewholders

import androidx.annotation.IntDef

/**
 * Тип содержимого элемента.
 *
 * @author ra.geraskin
 */
@IntDef(ITEM, DIVIDER, CUSTOM_VIEW)
annotation class ViewType

internal const val ITEM = 0
internal const val DIVIDER = 1
internal const val CUSTOM_VIEW = 2
