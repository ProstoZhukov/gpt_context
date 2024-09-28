package ru.tensor.sbis.widget_player.layout.inline

import android.view.View

/**
 * @author am.boldinov
 */
internal interface InlineMeasurer {

    fun measure(view: View, widthMeasureSpec: Int, heightMeasureSpec: Int, withMargins: Boolean = true)
}