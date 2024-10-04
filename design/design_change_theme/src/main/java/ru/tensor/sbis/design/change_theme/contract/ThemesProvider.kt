package ru.tensor.sbis.design.change_theme.contract

import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича для получения тем приложения.
 *
 * @author da.zolotarev
 */
interface ThemesProvider : Feature {

    /**
     *  Получить список тем приложения.
     */
    fun getThemes(): List<Theme>

    /**
     *  Получить объект с темами для использования с дневным и ночным системными режимами.
     *  Если метод вернёт null, то темиация на основе системных (ночного и дневного) режимов в приложении
     *      применяться не будет.
     */
    fun getSystemThemes(): SystemThemes?
}