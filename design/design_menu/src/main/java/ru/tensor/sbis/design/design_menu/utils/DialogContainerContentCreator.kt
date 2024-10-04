package ru.tensor.sbis.design.design_menu.utils

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.ViewContent
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.R

/**
 * Фабрика контента меню для контейнера.
 *
 * @author ra.geraskin
 */
internal class DialogContainerContentCreator(val menu: SbisMenu, @DimenRes val maxWidthRes: Int? = null) :
    ContentCreator<ViewContent> {
    override fun createContent(): ViewContent {
        return object : ViewContent {
            override fun getView(containerFragment: SbisContainerImpl, container: ViewGroup): View {
                val containerViewModel = containerFragment.getViewModel()

                containerFragment.lifecycleScope.launch {
                    containerFragment.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        containerViewModel.onDismissContainer.collect {
                            menu.closeMenu()
                        }
                    }
                }

                menu.showSubMenuListener = {
                    containerViewModel.showNewContent(DialogContainerContentCreator(it, maxWidthRes))
                }
                menu.addCloseListener {
                    containerViewModel.closeContainer()
                }
                return menu.createView(containerFragment.requireContext(), container, maxWidthRes)
            }

            override fun customWidth(): Int {
                return maxWidthRes ?: ResourcesCompat.ID_NULL
            }

            override fun useDefaultHorizontalOffset(): Boolean = false

            override fun theme(): Int = R.style.ContainerTheme
        }
    }
}