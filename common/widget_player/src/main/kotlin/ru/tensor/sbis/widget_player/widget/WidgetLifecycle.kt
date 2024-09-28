package ru.tensor.sbis.widget_player.widget

import android.view.ViewGroup

/**
 * @author am.boldinov
 */
interface WidgetLifecycle {

    fun onAttachedToPlayer(parent: ViewGroup) {

    }

    fun onDetachedFromPlayer(parent: ViewGroup) {

    }
}