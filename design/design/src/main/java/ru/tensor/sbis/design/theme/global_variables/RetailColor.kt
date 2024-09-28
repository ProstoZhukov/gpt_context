package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.RetailColorModel

/**
 * Линейка розничных цветов из глобальных переменных.
 *
 * Реализует [RetailColorModel].
 *
 * @author mb.kruglova
 */
enum class RetailColor(
    @AttrRes private val colorAttrRes: Int
) : RetailColorModel {

    /**
     * Цвет заголовка.
     */
    HEADER_BACKGROUND(R.attr.rtlHeaderBackground),

    /**
     * Цвет фона для клавиатурной панели.
     */
    KEYBOARD_PANEL_BACKGROUND(R.attr.rtlKeyboardPanelBackground),

    /**
     * Цвет фона активного списка в двухколоночном реестре.
     */
    LIST_ACTIVE_BACKGROUND(R.attr.rtlListActiveBackground);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}