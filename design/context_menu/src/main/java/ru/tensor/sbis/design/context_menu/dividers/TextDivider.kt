package ru.tensor.sbis.design.context_menu.dividers

import ru.tensor.sbis.design.context_menu.Item

/**
 * Модель представляющая разделитель с текстом внутри контекстного меню.
 * @param title текст внутри разделителя.
 * @param alignment выравнивание текста.
 * @author ma.kolpakov
 */
class TextDivider(val title: String? = null, val alignment: TextDividerAlignment = TextDividerAlignment.CENTER) : Item

enum class TextDividerAlignment {

    /** @SelfDocumented */
    LEFT,

    /** @SelfDocumented */
    CENTER,

    /** @SelfDocumented */
    RIGHT
}