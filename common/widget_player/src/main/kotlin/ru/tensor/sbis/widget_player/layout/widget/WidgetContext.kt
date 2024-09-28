package ru.tensor.sbis.widget_player.layout.widget

import android.content.Context
import android.content.MutableContextWrapper
import android.view.LayoutInflater

/**
 * @author am.boldinov
 */
class WidgetContext internal constructor(base: Context) : MutableContextWrapper(base) {

    val layoutInflater get() = LayoutInflater.from(this)!!
}