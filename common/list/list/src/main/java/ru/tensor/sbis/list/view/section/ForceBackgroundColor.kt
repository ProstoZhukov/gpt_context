package ru.tensor.sbis.list.view.section

/**
 * Использовать принудительный цвет фона
 * @author ma.kolpakov
 */
enum class ForceBackgroundColor {
    /**
     * Не использовать
     */
    NONE,

    /**
     * Темный цвет (атрибут list_content_dark_background)
     */
    DARK,

    /**
     * Светлый цвет (атрибут list_content_background)
     */
    WHITE
}