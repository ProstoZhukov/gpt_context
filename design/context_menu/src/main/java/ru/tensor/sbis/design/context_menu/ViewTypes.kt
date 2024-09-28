package ru.tensor.sbis.design.context_menu

import androidx.annotation.IntDef

/**
 * Тип содержимого элемента.
 *
 * @author ma.kolpakov
 */
@IntDef(ITEM, DIVIDER, DIVIDER_TEXT, CUSTOM_VIEW)
annotation class ViewType

internal const val ITEM = 0
internal const val DIVIDER = 1
internal const val DIVIDER_TEXT = 2
internal const val CUSTOM_VIEW = 3
