package ru.tensor.sbis.design.toolbar.appbar.statusbar

/**
 * Вспомогательный интерфейс для скрытия и отображения Statusbar
 *
 * @author ma.kolpakov
 * @since 12/24/2019
 */
@Suppress("unused")
internal interface StatusBarHelper {

    /**
     * Скрыть статус бар
     */
    fun hideStatusBar()

    /**
     * Показать статус бар
     */
    fun showStatusBar()
}