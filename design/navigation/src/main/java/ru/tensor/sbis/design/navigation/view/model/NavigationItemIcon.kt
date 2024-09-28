package ru.tensor.sbis.design.navigation.view.model

import androidx.annotation.StringRes

/**
 * Описание иконки элемента меню.
 *
 * @param default иконка по умолчанию. Используется в "невыбранном" состоянии.
 * @param selected иконка, которая используется, когда элементы "выбран".
 * @param isVisible должна ли иконка быть видимой.
 *
 * @author ma.kolpakov
 */
data class NavigationItemIcon @JvmOverloads constructor(
    @param:StringRes @get:StringRes val default: Int,
    @param:StringRes @get:StringRes val selected: Int = default,
    val isVisible: Boolean = true,
    val calendarDate: Int? = null
)