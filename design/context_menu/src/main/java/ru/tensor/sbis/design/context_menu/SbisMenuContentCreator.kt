package ru.tensor.sbis.design.context_menu

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.ViewContent

/**
 * Фабрика контента меню для контейнера.
 * @author ma.kolpakov
 */
internal class SbisMenuContentCreator(val menu: SbisMenu, @DimenRes val maxWidthRes: Int? = null) :
    ContentCreator<ViewContent> {
    override fun createContent(): ViewContent {
        return object : ViewContent {
            override fun getView(containerFragment: SbisContainerImpl, container: ViewGroup): View {
                val containerViewModel = containerFragment.getViewModel()

                containerFragment.viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    containerViewModel.onDismissContainer.collect {
                        menu.closeMenu(container.context)
                    }
                }

                menu.showSubMenuListener = {
                    containerViewModel.showNewContent(SbisMenuContentCreator(it, maxWidthRes))
                }
                menu.addCloseListener {
                    containerViewModel.closeContainer()
                }
                return menu.createMenuView(containerFragment.requireContext(), container, maxWidthRes)
            }

            override fun customWidth(): Int {
                return maxWidthRes ?: ResourcesCompat.ID_NULL
            }

            override fun useDefaultHorizontalOffset(): Boolean = false
        }
    }
}