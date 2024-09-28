package ru.tensor.sbis.design.toolbar.appbar.model

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Модель цветовой схемы для [SbisAppBarLayout]
 *
 * @property mainColor цвет, который считается преобладающим в графической шапке. Как правило, в этот параметр нужно
 * задавать доминантный цвет изображения в [ImageBackground] или цвет, который контрастирует с [ColorBackground]
 * @property darkText режим отображения цвета - тёмный или светлый. Нужно устанавливать таким образом, чтобы цвет
 * контрастировал с [mainColor]
 * @property canChangeStatusBarLightMode возможность менять цвет иконок статус бара, в зависимости от [darkText]
 * @property fillOpaqueWhenCollapsed должен ли быть у шапки в свёрнутом состоянии непрозрачный фон (иначе применяется
 * заливка с прозрачностью)
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
@Parcelize
data class ColorModel(
    @ColorInt
    val mainColor: Int,
    val darkText: Boolean,
    val canChangeStatusBarLightMode: Boolean = true,
    val fillOpaqueWhenCollapsed: Boolean = false
) : Parcelable {

    /**
     * Конструктор для установки преобладающего цвета в формате строки
     *
     * @see Color.parseColor
     */
    constructor(
        colorString: String,
        darkText: Boolean,
        canChangeStatusBarLightMode: Boolean = true,
        fillOpaqueWhenCollapsed: Boolean = false
    ) : this(Color.parseColor(colorString), darkText, canChangeStatusBarLightMode, fillOpaqueWhenCollapsed)
}