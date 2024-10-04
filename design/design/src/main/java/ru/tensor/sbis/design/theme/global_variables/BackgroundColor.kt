package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BackgroundColorModel

/**
 * Линейка цветов фона из глобальных переменных.
 *
 * Реализует [BackgroundColorModel].
 *
 * @author mb.kruglova
 */
enum class BackgroundColor(
    @AttrRes private val colorAttrRes: Int
) : BackgroundColorModel {

    /**
     * Основной фон.
     */
    DEFAULT(com.google.android.material.R.attr.backgroundColor),

    /**
     * Контрастный фон.
     */
    CONTRAST(R.attr.contrastBackgroundColor),

    /**
     * Фон в режиме только для чтения.
     */
    READ_ONLY(R.attr.readonlyBackgroundColor),

    /**
     * Фон при нажатии.
     */
    ACTIVE(R.attr.activeBackgroundColor),

    /**
     * Затемнение фона окон и панелей.
     */
    DIM(R.attr.dimBackgroundColor),

    /**
     * Фон плитки.
     */
    TILE(R.attr.itemBackgroundColorTile),

    /**
     * Фон тулбара.
     */
    HEADER(R.attr.headerBackgroundColor),

    /**
     * Фон выезжающей панели
     */
    STACK(R.attr.backgroundColorStack),

    /**
     * Фон шапки выезжающей панели
     */
    HEADER_STACK(R.attr.headerBackgroundColorStack),

    /**
     * Фон диалога
     */
    DIALOG(R.attr.backgroundColorDialog),

    /**
     * Фон всплывающей подсказки
     */
    INFOBOX(R.attr.backgroundColorInfobox),

    /**
     * Фон меню
     */
    STICKY(R.attr.backgroundColorSticky),

    /**
     * Фон под прозрачным изображением
     */
    IMAGE(R.attr.imageBackgroundColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}