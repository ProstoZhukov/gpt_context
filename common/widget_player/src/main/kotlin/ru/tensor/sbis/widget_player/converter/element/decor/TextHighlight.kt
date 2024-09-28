package ru.tensor.sbis.widget_player.converter.element.decor

import ru.tensor.sbis.widget_player.res.color.ColorRes
import ru.tensor.sbis.widget_player.res.color.valueRaw

/**
 * Декларирует данные для подсветки (выделения фоном) частей текста в виджетах.
 *
 * @property words список со строками для подсветки
 * @property backgroundColor цвет выделения текста при нахождении [words] в тексте виджета.
 *
 * @author am.boldinov
 */
data class TextHighlight(
    val words: List<String> = emptyList(),
    val backgroundColor: ColorRes = ColorRes.valueRaw("#FFFDA6")
)