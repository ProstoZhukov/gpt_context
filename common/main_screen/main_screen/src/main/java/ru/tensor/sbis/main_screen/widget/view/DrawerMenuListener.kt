package ru.tensor.sbis.main_screen.widget.view

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SideMenuSlideListener

/**
 * Реализация [DrawerLayout.DrawerListener] для оповещения текущего [ContentController] о событиях выезжающего меню.
 *
 * @author kv.martyshenko
 */
internal class DrawerMenuListener(
    private val contentContainer: ContentContainer,
    private val contentControllerProvider: () -> ContentController?
) : DrawerLayout.DrawerListener {

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        getSideMenuListener()?.onDrawerSlide(drawerView, slideOffset, contentContainer)
    }

    override fun onDrawerOpened(drawerView: View) {
        getSideMenuListener()?.onDrawerOpened(drawerView, contentContainer)
    }

    override fun onDrawerClosed(drawerView: View) {
        getSideMenuListener()?.onDrawerClosed(drawerView, contentContainer)
    }

    override fun onDrawerStateChanged(newState: Int) {
        getSideMenuListener()?.onDrawerStateChanged(newState, contentContainer)
    }

    private fun getSideMenuListener(): SideMenuSlideListener? {
        return contentControllerProvider()?.let { it as? SideMenuSlideListener }
    }

}