package ru.tensor.sbis.widget_player.layout

import android.graphics.Rect
import android.view.View

/**
 * @author am.boldinov
 */
interface VisibleOnScreenContentView {

    val screenPositionHandler: ScreenPositionViewHandler

}

fun interface ScreenPositionViewHandler {
    fun onScreenPositionChanged(view: View, position: Rect, screen: Rect)
}

abstract class AttachDetachScreenViewHandler : ScreenPositionViewHandler {

    private var isAttachedToUserScreen: Boolean? = null

    override fun onScreenPositionChanged(view: View, position: Rect, screen: Rect) {
        if (Rect.intersects(screen, position)) {
            dispatchAttachToUserScreen()
        } else {
            dispatchDetachFromUserScreen()
        }
    }

    fun isAttachedToUserScreen() = isAttachedToUserScreen == true

    protected abstract fun onAttachToUserScreen()

    protected abstract fun onDetachFromUserScreen()

    private fun dispatchAttachToUserScreen() {
        if (isAttachedToUserScreen == null || isAttachedToUserScreen == false) {
            isAttachedToUserScreen = true
            onAttachToUserScreen()
        }
    }

    private fun dispatchDetachFromUserScreen() {
        if (isAttachedToUserScreen == null || isAttachedToUserScreen == true) {
            isAttachedToUserScreen = false
            onDetachFromUserScreen()
        }
    }
}