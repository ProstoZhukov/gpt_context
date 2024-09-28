package ru.tensor.sbis.widget_player.layout

import android.graphics.Rect
import android.view.View

/**
 * @author am.boldinov
 */
class MeasureSize {

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    fun set(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun setEmpty() {
        width = 0
        height = 0
    }

    fun setFrom(view: View) {
        set(view.measuredWidth, view.measuredHeight)
    }

    fun setFrom(rect: Rect) {
        set(rect.width(), rect.height())
    }

    fun isEmpty(): Boolean {
        return width == 0 && height == 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeasureSize

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }

}