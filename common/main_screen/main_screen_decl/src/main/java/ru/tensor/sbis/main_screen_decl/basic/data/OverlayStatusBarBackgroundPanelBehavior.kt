package ru.tensor.sbis.main_screen_decl.basic.data

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Определяет поведение показа/скрытия панели, выступающей в качестве фона статусбара.
 *
 * @author us.bessonov
 */
interface OverlayStatusBarBackgroundPanelBehavior {

    /**
     * Изменить видимость панели при добавлении нового экрана.
     *
     * @param hasMainContent включает ли текущий набор фрагментов в [fragmentManager] основной контент, отображаемый
     * под шапкой.
     */
    fun onFragmentViewAdded(
        statusBarBackgroundPanel: View,
        fragment: Fragment,
        fragmentManager: FragmentManager,
        hasMainContent: Boolean
    )

    /**
     * Изменить видимость панели при удалении экрана.
     *
     * @param hasMainContent включает ли текущий набор фрагментов в [fragmentManager] основной контент, отображаемый
     * под шапкой.
     */
    fun onFragmentViewRemoved(
        statusBarBackgroundPanel: View,
        fragment: Fragment,
        fragmentManager: FragmentManager,
        hasMainContent: Boolean
    )
}

