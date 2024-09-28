package ru.tensor.sbis.widget_player.layout.inline

/**
 * @author am.boldinov
 */
internal interface InlineOffset {

    val x: Int

    val y: Int
}

internal class MutableInlineOffset : InlineOffset {
    override var x: Int = 0
    override var y: Int = 0

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun copyFrom(offset: InlineOffset) {
        x = offset.x
        y = offset.y
    }
}