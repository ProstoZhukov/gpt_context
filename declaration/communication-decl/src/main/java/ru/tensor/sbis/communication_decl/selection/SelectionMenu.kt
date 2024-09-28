package ru.tensor.sbis.communication_decl.selection

import androidx.fragment.app.FragmentManager

/**
 * API компонента меню выбора.
 *
 * @author vv.chekurda
 */
interface SelectionMenu : SelectionMenuDelegate.Provider {

    /**
     * Установить фрагмент меню в заданный контейнер [containerId].
     */
    fun setupMenu(fragmentManager: FragmentManager, containerId: Int)
}