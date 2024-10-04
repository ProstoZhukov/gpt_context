package ru.tensor.sbis.design.files_picker.view.ui

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabClickAction
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegate
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter

/**
 * Роутер для экрана "Компонент выбора файлов".
 *
 * @author ai.abramenko
 */
internal class FilesPickerRouter(@IdRes private val contentContainerId: Int) : Router<Fragment>, FragmentRouter() {

    fun showTabScreen(tabClickAction: SbisFilesPickerTabClickAction) {
        execute {
            when (tabClickAction) {
                is SbisFilesPickerTabClickAction.Custom -> tabClickAction.action(this)
                is SbisFilesPickerTabClickAction.ShowFragment -> {
                    val newFragment = tabClickAction.fragmentFactory()
                    val tag = newFragment.javaClass.name
                    if (childFragmentManager.findFragmentByTag(tag) == null) {
                        childFragmentManager
                            .beginTransaction()
                            .replace(contentContainerId, newFragment, tag)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    fun showMenu(
        menu: SbisMenu,
        verticalLocator: AnchorVerticalLocator,
        horizontalLocator: AnchorHorizontalLocator
    ) {
        execute {
            menu.showMenuWithLocators(childFragmentManager, verticalLocator, horizontalLocator)
        }
    }

    fun close() {
        execute {
            val parentFragment = parentFragment
            if (parentFragment == null) {
                illegalState { "Unexpected parent fragment." }
                return@execute
            }
            fun applyClose() {
                when (parentFragment) {
                    is ContainerMovableDelegate -> parentFragment.closeContainer()
                    is Container.Closeable -> parentFragment.closeContainer()
                    is DialogFragment -> parentFragment.dismiss()
                }
            }
            applyClose()
        }
    }

    fun navigateBack(): Boolean {
        var isNavigate = false
        execute {
            val childFragments = childFragmentManager.fragments
            isNavigate = if (childFragments.size > 0) {
                (childFragments.last() as? FragmentBackPress)?.onBackPressed() ?: false
            } else {
                false
            }
        }
        return isNavigate
    }
}