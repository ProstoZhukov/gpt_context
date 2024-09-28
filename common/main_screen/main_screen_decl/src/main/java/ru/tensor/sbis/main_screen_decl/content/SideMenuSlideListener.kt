package ru.tensor.sbis.main_screen_decl.content

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout

/**
 * Аналог [DrawerLayout.DrawerListener] для реагирования на события выезжающего меню
 *
 * @author kv.martyshenko
 */
interface SideMenuSlideListener {

    /**
     * @see [DrawerLayout.DrawerListener.onDrawerSlide]
     */
    @Suppress("EmptyMethod")
    fun onDrawerSlide(
        drawerView: View,
        slideOffset: Float,
        contentContainer: ContentContainer
    )

    /**
     * @see [DrawerLayout.DrawerListener.onDrawerOpened]
     */
    fun onDrawerOpened(
        drawerView: View,
        contentContainer: ContentContainer
    )

    /**
     * @see [DrawerLayout.DrawerListener.onDrawerClosed]
     */
    fun onDrawerClosed(
        drawerView: View,
        contentContainer: ContentContainer
    )

    /**
     * @see [DrawerLayout.DrawerListener.onDrawerStateChanged]
     */
    fun onDrawerStateChanged(
        newState: Int,
        contentContainer: ContentContainer
    )

}