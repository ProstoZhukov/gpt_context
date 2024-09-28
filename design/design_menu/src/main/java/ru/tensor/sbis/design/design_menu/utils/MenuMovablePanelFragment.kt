package ru.tensor.sbis.design.design_menu.utils

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.design.container.Content
import ru.tensor.sbis.design.design_menu.R
import ru.tensor.sbis.design.design_menu.SbisMenu
import ru.tensor.sbis.design.design_menu.api.BaseMenuItem
import ru.tensor.sbis.design.design_menu.api.MenuItemClickListener
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.databinding.MenuInPanelBinding
import ru.tensor.sbis.design.design_menu.view.shadow.ShadowPosition
import ru.tensor.sbis.design.design_menu.view.shadow.ShadowView
import ru.tensor.sbis.design.design_menu.viewholders.MenuAdapter
import ru.tensor.sbis.design.utils.extentions.hide
import ru.tensor.sbis.design.utils.extentions.show
import ru.tensor.sbis.design.view_ext.viewbinding.viewBinding
import ru.tensor.sbis.design_dialogs.dialogs.container.Container

/**
 * Фрагмент для отображения меню в шторке.
 *
 * @author ra.geraskin
 */
internal class MenuMovablePanelFragment(
    private var menu: SbisMenu = SbisMenu(children = emptyList())
) : Fragment(R.layout.menu_in_panel), Content {

    private val binding by viewBinding(MenuInPanelBinding::bind)
    private val hierarchyBackStack = mutableListOf<SbisMenu>()
    private var previousMenuItemToScroll: MenuItem? = null
    private var isClosed: Boolean = false
    private lateinit var topShadowView: ShadowView
    private lateinit var bottomShadowView: ShadowView
    private val styleHolder by lazy(LazyThreadSafetyMode.NONE) {
        SbisMenuStyleHolder.createStyleHolderForPanel(requireContext(), menu.selectionStyle)
    }

    /**
     * Слушатель изменения layout списка. Необходим для корректной работы во время движения (изменения размера) шторки,
     * т.к. обычный [scrollListener][RecyclerView.OnScrollListener] отрабатывают некорректно.
     */
    private val onRecyclerLayoutChangeListener: View.OnLayoutChangeListener by lazy(LazyThreadSafetyMode.NONE) {
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            with(binding) {
                val canScrollToTop = menuRecycler.canScrollVertically(RECYCLER_VIEW_TOP_DIRECTION)
                val canScrollToBottom = menuRecycler.canScrollVertically(RECYCLER_VIEW_BOTTOM_DIRECTION)

                topShadowView.apply {
                    when {
                        canScrollToTop && !isVisible -> show(SHADOW_FADE_ANIMATION_DURATION)
                        !canScrollToTop && isVisible -> hide(SHADOW_FADE_ANIMATION_DURATION)
                    }
                }

                bottomShadowView.apply {
                    when {
                        canScrollToBottom && !isVisible -> show(SHADOW_FADE_ANIMATION_DURATION)
                        !canScrollToBottom && isVisible -> hide(SHADOW_FADE_ANIMATION_DURATION)
                    }
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) return
        if (menu.children.isNotEmpty()) return
        menu = savedInstanceState.getParcelableUniversally<SbisMenu>(MENU_KEY) ?: SbisMenu(children = emptyList())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(MENU_KEY, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        hierarchyBackStack.add(menu)
        val menuAdapter = MenuAdapter(
            hideDefaultDividers = menu.hideDefaultDividers,
            selectionEnabled = menu.selectionEnabled,
            hasTitle = menu.title != null,
            styleHolder = styleHolder,
            minItemWidth = ViewGroup.LayoutParams.MATCH_PARENT,
            containerType = ContainerType.PANEL,
            twoLinesItemsTitle = menu.twoLinesItemsTitle
        )
        menuRecycler.adapter = menuAdapter

        bottomShadowView = ShadowView(requireContext(), ShadowPosition.BOTTOM, styleHolder).apply {
            menuRecycler.addOnScrollListener(scrollListener)
            menuListContainer.addView(this)
        }

        topShadowView = ShadowView(requireContext(), ShadowPosition.TOP, styleHolder).apply {
            menuRecycler.addOnScrollListener(scrollListener)
            menuListContainer.addView(this)
        }

        updateItemListByBackStack(menuAdapter)

        menuAdapter.clickListener = { item ->
            previousMenuItemToScroll = null
            if (item is SbisMenu) {
                hierarchyBackStack.add(item)
                updateItemListByBackStack(menuAdapter)
            } else {
                getOnMenuItemClickListener()?.onClick(item as BaseMenuItem) ?: item.handler?.invoke()
                closePanel()
            }
        }

        menuHeaderBackButton.setOnClickListener {
            previousMenuItemToScroll = hierarchyBackStack.last()
            hierarchyBackStack.removeLast()
            updateItemListByBackStack(menuAdapter)
        }
    }

    private fun updateItemListByBackStack(adapter: MenuAdapter) = with(binding) {
        val currentMenu = hierarchyBackStack.last()
        adapter.setItems(currentMenu.children, requireContext())

        val scrollPosition = adapter.getItemPosition(previousMenuItemToScroll)
        if (scrollPosition == -1) menuRecycler.layoutManager?.scrollToPosition(0)
        else menuRecycler.layoutManager?.scrollToPosition(scrollPosition)
        menuHeaderTitle.text = currentMenu.title
        if (currentMenu.footer != null) {
            menuFooterContainer.visibility = View.VISIBLE
            menuFooterContainer.addView(currentMenu.footer.invoke(requireContext(), menuFooterContainer))
        } else {
            menuFooterContainer.visibility = View.GONE
        }
        menuHeaderBackButton.isVisible = hierarchyBackStack.size > 1
        menuHeaderContainer.isVisible = currentMenu.title != null || hierarchyBackStack.size > 1
        menuRecycler.addOnLayoutChangeListener(onRecyclerLayoutChangeListener)
    }

    override fun onDestroyView() {
        binding.menuRecycler.removeOnLayoutChangeListener(onRecyclerLayoutChangeListener)
        super.onDestroyView()
    }

    private fun closePanel() {
        if (isClosed) return
        if (parentFragment is Container.Closeable) {
            (parentFragment as Container.Closeable).closeContainer()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        isClosed = true
    }

    private fun getOnMenuItemClickListener(): MenuItemClickListener? =
        parentFragment?.parentFragmentManager?.fragments?.filterIsInstance<MenuItemClickListener>()?.firstOrNull()
            ?: parentFragment?.requireActivity() as? MenuItemClickListener

    private companion object {
        const val MENU_KEY = "MENU_KEY"
        const val RECYCLER_VIEW_TOP_DIRECTION = -1
        const val RECYCLER_VIEW_BOTTOM_DIRECTION = 1
        const val SHADOW_FADE_ANIMATION_DURATION = 500L
    }
}