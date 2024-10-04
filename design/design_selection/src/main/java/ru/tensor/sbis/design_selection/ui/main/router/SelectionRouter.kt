package ru.tensor.sbis.design_selection.ui.main.router

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.ui.content.SelectionContentFragment

/**
 * Роутер компонента выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionRouter(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int,
    useReplaceStrategy: Boolean = false
) {

    private val setupFragment: FragmentTransaction.(Int, Fragment) -> FragmentTransaction =
        if (useReplaceStrategy) {
            FragmentTransaction::replace
        } else {
            FragmentTransaction::add
        }

    /**
     * Открыть папку [folderItem].
     */
    fun openFolder(folderItem: SelectionFolderItem) {
        fragmentManager.beginTransaction()
            .setupFragment(containerId, SelectionContentFragment.newInstance(folderItem))
            .addToBackStack(null)
            .commit()
    }

    /**
     * Закрыть все папки до корневой.
     */
    fun closeAllFolders() {
        while (back()) Unit
    }

    /**
     * Обработать действие для перехода назад по стеку.
     */
    fun back(): Boolean =
        fragmentManager.popBackStackImmediate()
}