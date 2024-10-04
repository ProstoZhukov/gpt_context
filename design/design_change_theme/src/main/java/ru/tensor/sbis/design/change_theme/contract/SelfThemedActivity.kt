package ru.tensor.sbis.design.change_theme.contract

import androidx.annotation.StyleRes

/**
 * Интерфейс для активити, которые имеют свою тему.
 *
 * Необходим для того, чтобы при смене темы приложения, тема активити была применена.
 */
interface SelfThemedActivity {

    /**
     * Возвращает тему активити
     *
     * Пример:
     * ```
     * override fun getThemeRes() =
     * ThemeContextBuilder(this, ResourcesCompat.ID_NULL, R.style.ActivityTheme).buildThemeRes()
     * ```
     */
    @StyleRes
    fun getThemeRes(): Int
}