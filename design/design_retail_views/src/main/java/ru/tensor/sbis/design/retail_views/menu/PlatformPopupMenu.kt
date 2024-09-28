package ru.tensor.sbis.design.retail_views.menu

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.*
import ru.tensor.sbis.design.context_menu.BaseItem
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState.*
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Класс-обертка над компонентом SbisMenu
 */
@Suppress("EmptyMethod", "SameReturnValue", "unused")
class PlatformPopupMenu(
    private val context: Context,
    private val usePopupWindowShadow: Boolean,
    private val isSelectable: Boolean,
    private val isFocusable: Boolean
) {

    private var menu: SbisMenu? = null
    private var menuContainer: ViewGroup? = null
    private val items = mutableListOf<Item>()

    companion object {
        private const val SHOW_DELAY_MILLIS = 500
        private const val IS_POP_UP_MENU_DISPLAYED = "IS_POP_UP_MENU_DISPLAYED"
    }

    constructor(context: Context) : this(context, false)

    constructor(context: Context, usePopupWindowShadow: Boolean, isSelectable: Boolean = false) :
        this(context, usePopupWindowShadow, isFocusable = true, isSelectable = isSelectable)

    /** Индекс выбранного элемента. */
    var selectedItemIndex: Int = -1

    /**
     * Установка элементов меню.
     * @param items - список элементов.
     */
    fun setItems(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
    }

    /**
     * Установка активного элемента по заголовку.
     * @param title - заголовок элемента.
     */
    fun setActiveItemByTitle(title: String) {
        selectedItemIndex = items.indexOfFirst { (it.item as BaseItem).title == PlatformSbisString.Value(title) }
    }

    /**
     * Проверка списка элементов на пустоту.
     */
    fun hasItems(): Boolean {
        return items.isNotEmpty()
    }

    /**
     * Функция отображения меню.
     * @param fragmentManager - [FragmentManager]
     * @param anchor - объект-"якорь", относительно которого строится меню
     * @param title - заголовок меню
     * @param customWidth - кастомная ширина меню
     * @param closeListener - callback на закрытие меню
     */
    fun showMenu(
        fragmentManager: FragmentManager,
        anchor: View,
        title: String? = null,
        @DimenRes customWidth: Int? = null,
        closeListener: (() -> Unit)? = null
    ): Boolean {
        return showMenu(
            fragmentManager = fragmentManager,
            anchor = anchor,
            title = title,
            customWidth = customWidth,
            customAnchorHorizontalLocator = null,
            customAnchorVerticalLocator = null,
            closeListener = closeListener
        )
    }

    /**
     * Функция отображения меню.
     * @param fragmentManager - [FragmentManager]
     * @param anchor - объект-"якорь", относительно которого строится меню
     * @param title - заголовок меню
     * @param customWidth - кастомная ширина меню
     * @param customAnchorHorizontalLocator - горизонтальный локатор меню
     * @param customAnchorVerticalLocator - вертикальный локатор меню
     * @param closeListener - callback на закрытие меню
     */
    fun showMenu(
        fragmentManager: FragmentManager,
        anchor: View,
        title: String? = null,
        @DimenRes
        customWidth: Int? = null,
        customAnchorHorizontalLocator: AnchorHorizontalLocator? = null,
        customAnchorVerticalLocator: AnchorVerticalLocator? = null,
        closeListener: (() -> Unit)? = null
    ): Boolean {
        createMenu(title)
        closeListener?.let {
            menu?.addCloseListener(closeListener)
        }
        menu?.let {
            val horizontalLocator = customAnchorHorizontalLocator ?: AnchorHorizontalLocator(
                HorizontalAlignment.LEFT,
                innerPosition = true
            )
            horizontalLocator.anchorView = anchor
            val verticalLocator = customAnchorVerticalLocator ?: AnchorVerticalLocator(
                VerticalAlignment.BOTTOM,
                innerPosition = false
            )
            verticalLocator.anchorView = anchor
            if (customWidth != null) {
                it.showMenuWithLocators(
                    fragmentManager,
                    verticalLocator,
                    horizontalLocator,
                    customWidth = customWidth,
                    dimType = DimType.SHADOW
                )
            } else {
                it.showMenuWithLocators(
                    fragmentManager,
                    verticalLocator,
                    horizontalLocator,
                    dimType = DimType.SHADOW
                )
            }
        }
        return true
    }

    private fun createMenu(title: String?) {
        val defaultItems = items.map { it.item }
        if (isSelectable) {
            defaultItems.filterIsInstance<MenuItem>().forEachIndexed { index, item ->
                item.state = if (index == selectedItemIndex) ON else MIXED

                val oldHandler = item.handler
                item.handler = {
                    oldHandler?.invoke()
                    selectedItemIndex = index
                }
            }
        }
        menu = SbisMenu(
            children = defaultItems,
            hideDefaultDividers = true,
            title = title,
            stateOnIcon = CheckboxIcon.MARKER
        )
        menu?.createMenuView(context, menuContainer)
    }
}
