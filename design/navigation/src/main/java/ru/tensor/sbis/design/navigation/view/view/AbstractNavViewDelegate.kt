package ru.tensor.sbis.design.navigation.view.view

import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.adapter.NavigationViewHelper
import ru.tensor.sbis.design.navigation.view.model.AllItemsUnselected
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.ItemSelectedByUser
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.SelectedSameItem

/**
 * Базовый класс-делегат для компонентов навигации со списком, который обеспечивает синхронизацию элементов
 * меню за счёт использования общего [NavAdapter].
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
internal class AbstractNavViewDelegate : NavigationView {

    private var selectedItem: NavigationItem? = null
    private var selectedParentItem: NavigationItem? = null

    /**
     * Вспомогательный объект, который будет использоваться для построения элемента списка.
     */
    private lateinit var viewHolderHelper: NavigationViewHelper

    /**
     * Контейнер списка элементов.
     */
    private lateinit var scrollView: FrameLayout

    /**
     * Cписок элементов навигации.
     */
    private lateinit var navList: NavListAPI

    /**
     * Инициализация ресайклера и аналитики.
     */
    internal fun init(
        viewHolderHelper: NavigationViewHelper,
        navList: NavListAPI,
        scrollView: FrameLayout
    ) {
        this.viewHolderHelper = viewHolderHelper
        this.navList = navList
        this.scrollView = scrollView
        this.navList.showItemListener = { onShowItem(it) }
    }

    /**
     * Управлять поведением при выборе иконок компоненты будут отдельно.
     */
    override fun setIsUsedNavigationIcons(isUsed: Boolean) = Unit
    override fun isUsedNavigationIcons() = false

    /**
     * @see [NavigationView.setAdapter]
     *
     * @throws IllegalStateException при повторной установке адаптера.
     */
    override fun setAdapter(navAdapter: NavAdapter<out NavigationItem>, lifecycleOwner: LifecycleOwner) {
        navAdapter.setController(viewHolderHelper, navList)
        navAdapter.navigationEvents.observe(lifecycleOwner) { event ->
            val (item, parent) = when (event) {
                is ItemSelected -> event.selectedItem to event.selectedItemParent
                is ItemSelectedByUser -> event.selectedItem to event.selectedItemParent
                is SelectedSameItem -> event.selectedItem to event.selectedItemParent
                null, AllItemsUnselected -> null to null
            }
            selectedItem = item
            selectedParentItem = parent
            scrollToSelectedItem()
        }
    }

    override fun hideItem(item: NavigationItem) = navList.hide(item)

    override fun showItem(item: NavigationItem) = navList.show(item)

    override fun changeItemIcon(item: NavigationItem, icons: ControllerNavIcon) {
        navList.changeItemIcon(item, icons)
    }

    override fun changeItemLabel(item: NavigationItem, label: NavigationItemLabel) {
        navList.changeItemLabel(item, label)
    }

    override fun setConfiguration(configuration: NavViewConfiguration) {
        navList.configuration = configuration
    }

    private fun onShowItem(item: NavigationItem) {
        if (selectedItem != item) return
        scrollToSelectedItem()
    }

    private fun scrollToSelectedItem() {
        val childView = selectedItem?.let { navList.getItemView(it) }
        val parentView = selectedParentItem?.let { navList.getItemView(it) }
        val itemView = if (childView != null && childView.isVisible) childView
        else if (parentView != null && parentView.isVisible)
            parentView
        else null
        itemView?.post {
            if (viewOnScreen(scrollView, itemView)) return@post
            when (scrollView) {
                is HorizontalScrollView -> {
                    (scrollView as HorizontalScrollView).smoothScrollTo(itemView.left, itemView.top)
                }

                is ScrollView -> {
                    (scrollView as ScrollView).smoothScrollTo(itemView.left, itemView.top)
                }
            }
        }
    }

    private fun viewOnScreen(scrollView: FrameLayout, itemView: View): Boolean {
        val scrollRect = Rect()
        val itemRect = Rect()

        itemView.getHitRect(itemRect)
        scrollView.getHitRect(scrollRect)

        itemRect.offset(-scrollView.scrollX + itemView.width, -scrollView.scrollY)

        return scrollRect.contains(itemRect)
    }

}
