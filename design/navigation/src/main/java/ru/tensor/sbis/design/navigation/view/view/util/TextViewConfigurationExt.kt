/**
 * Файл с функциями для настройки TextView в навигации
 */
package ru.tensor.sbis.design.navigation.view.view.util

import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * Установить шрифт и размер текста в зависимости от того, используется ли новый шрифт для иконок.
 */
internal fun SbisTextView.setTypefaceAndSize(isUsedNavigationIcons: Boolean) {
    typeface = if (isUsedNavigationIcons) TypefaceManager.getSbisNavigationIconTypeface(context) else
        TypefaceManager.getSbisMobileIconTypeface(context)
    paint.textSize = if (isUsedNavigationIcons) IconSize.X3L.getDimen(context) else IconSize.X7L.getDimen(context)
}