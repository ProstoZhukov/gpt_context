package ru.tensor.sbis.widget_player.widget.emoji

/**
 * @author am.boldinov
 */
data class Emoji(
    val char: String,
    val size: Float,
    val padding: Int = 0
) {
    val width = size.toInt() + padding * 2
    val height = width
}