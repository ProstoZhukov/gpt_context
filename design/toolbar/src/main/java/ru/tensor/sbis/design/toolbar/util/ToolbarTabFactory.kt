/**
 * Инструменты для упрощённого создания вкладок для верхней навигационной панели
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.toolbar.util

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout

/**
 * Создаёт вкладку с текстом и опциональными счётчиками
 */
fun createTitleToolbarTab(
    @IdRes idRes: Int,
    @StringRes titleRes: Int,
    newCounter: Int = 0,
    totalCounter: Int = 0,
    isEnabled: Boolean = true,
    counterFormatter: TabFormatter = DEFAULT_TAB_FORMAT
) = ToolbarTabLayout.ToolbarTab(
    idRes,
    titleRes,
    totalCounter,
    newCounter,
    isEnabled = isEnabled,
    counterFormatter = counterFormatter
)

/**
 * Создаёт вкладку с иконкой и опциональным счётчиком в виде бейджа
 */
fun createIconToolbarTab(
    @IdRes idRes: Int,
    @StringRes iconRes: Int,
    counter: Int = 0,
    isEnabled: Boolean = true,
    counterFormatter: TabFormatter = DEFAULT_TAB_FORMAT
) = ToolbarTabLayout.ToolbarTab(
    idRes,
    iconRes,
    isImageTab = true,
    primaryCounter = counter,
    isEnabled = isEnabled,
    counterFormatter = counterFormatter
)

/**
 * Создаёт вкладку с произвольным макетом
 */
fun createCustomLayoutToolbarTab(
    @IdRes idRes: Int,
    @LayoutRes layout: Int,
    @StringRes titleOrIconRes: Int,
    newCounter: Int = 0,
    totalCounter: Int = 0,
    isEnabled: Boolean = true,
    counterFormatter: TabFormatter = DEFAULT_TAB_FORMAT
) = ToolbarTabLayout.ToolbarTab(
    idRes,
    titleOrIconRes,
    totalCounter,
    newCounter,
    customLayout = layout,
    isEnabled = isEnabled,
    counterFormatter = counterFormatter
)