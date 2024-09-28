package ru.tensor.sbis.widget_player.layout.inline

/**
 * @author am.boldinov
 */
internal interface LineBounds {

    val top: Int

    val left: Int

    val right: Int

    val bottom: Int

    fun height() = bottom - top

    fun width() = right - left
}

internal class MutableLineBounds : LineBounds {
    override var top: Int = 0
    override var left: Int = 0
    override var right: Int = 0
    override var bottom: Int = 0
}
