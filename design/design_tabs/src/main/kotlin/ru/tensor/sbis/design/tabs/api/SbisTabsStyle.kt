package ru.tensor.sbis.design.tabs.api

import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen

/**
 * Модель кастомного стиля панели вкладок.
 *
 * @author da.zolotarev
 */
data class SbisTabsStyle(
    /** Цвет текста выбранной вкладки. */
    val customSelectedTitleColor: SbisColor? = null,
    /** Цвет текста невыбранных вкладок. */
    val customUnselectedTitleColor: SbisColor? = null,
    /** Использовать ли жирный шрифт для вкладок. */
    val useMediumTitleFontStyle: Boolean = false,
    /** Размер текста вкладок. */
    val customTitleFontSize: SbisDimen? = null,
    /** Цвет маркера (нижнего подчеркивания выделенной вкладки). */
    val customMarkerColor: SbisColor? = null
)