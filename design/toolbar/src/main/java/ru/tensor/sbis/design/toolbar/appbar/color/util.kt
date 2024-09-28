/**
 * Набор инструментов для определения правил обновления цвета для View. Новые правила добавляются как плагины:
 * 1. Добавить реализацию ColorUpdateFunction
 * 2. Добавить ветку для получения функции в метод getColorUpdateFunction()
 *
 * @author ma.kolpakov
 * Создан 9/29/2019
 */
@file:JvmName("ColorFunctionUtil")

package ru.tensor.sbis.design.toolbar.appbar.color

import android.view.View
import androidx.appcompat.widget.Toolbar
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.toolbar.Toolbar as SbisToolbar

internal val TOOLBAR_COLOR_UPDATE_FUNCTION = ToolbarColorUpdateFunction()
internal val SBIS_TOOLBAR_COLOR_UPDATE_FUNCTION = SbisToolbarColorUpdateFunction()

/**
 * Метод получения [ColorUpdateFunction] по типу [view]
 */
@Suppress("UNCHECKED_CAST" /* Безопасность типов гарантируется внутренними правилами */)
internal fun <ViewType : View> getColorUpdateFunction(view: ViewType): ColorUpdateFunction<ViewType> = when (view) {
    is CollapsingToolbarLayout -> CollapsingLayoutColorUpdateFunction
    is SbisTextView -> TextViewColorUpdateFunction
    is Toolbar -> TOOLBAR_COLOR_UPDATE_FUNCTION
    is SbisToolbar -> SBIS_TOOLBAR_COLOR_UPDATE_FUNCTION
    else -> throw IllegalArgumentException("ColorUpdateFunction is not defined for view type '${view::class.java}'")
} as ColorUpdateFunction<ViewType>