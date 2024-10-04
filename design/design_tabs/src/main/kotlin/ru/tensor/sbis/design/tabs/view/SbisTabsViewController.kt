package ru.tensor.sbis.design.tabs.view

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.withStyledAttributes
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.tabs.R
import ru.tensor.sbis.design.tabs.TabsPlugin
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsStyle
import ru.tensor.sbis.design.tabs.api.SbisTabsViewApi
import ru.tensor.sbis.design.tabs.api.SbisTabsViewApiInternal
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.tabItem.SbisTabItemStyleHolder
import ru.tensor.sbis.design.tabs.tabItem.SbisTabView
import ru.tensor.sbis.design.tabs.util.SbisTabInternalStyle
import ru.tensor.sbis.design.tabs.util.SbisTabsViewAdapter
import ru.tensor.sbis.design.tabs.util.TabViewPool
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.design.utils.loadEnum
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import java.util.LinkedList

/**
 * Логика работы [SbisTabsView].
 *
 * @author da.zolotarev
 */
class SbisTabsViewController(
    private var publishScope: CoroutineScope = CoroutineScope(SupervisorJob())
) : SbisTabsViewApi, SbisTabsViewApiInternal {
    private lateinit var tabsView: SbisTabsView
    private lateinit var tabsViewPool: TabViewPool

    private val itemViews: LinkedList<SbisTabView>
        get() = tabsView.itemViews

    private val tabsContainer: LinearLayoutWithMarker
        get() = tabsView.tabsContainer

    private val tabsController: ToolbarTabsController?
        get() = TabsPlugin.tabsController

    private val adapter by lazy { SbisTabsViewAdapter(tabsView) }

    private var onTabClickListener: (SbisTabsViewItem) -> Unit = {}

    private var mIsAccent: SbisTabInternalStyle = SbisTabInternalStyle.ACCENTED
        set(value) {
            if (field == value) return
            field = value
            styleHolder.setStyle(tabsView.context, field, style)
            tabStyleHolder.setStyle(tabsView.context, field, style)
            itemViews.forEach { it.updateStyleHolder() }
            tabsView.requestLayout()
        }

    private val selectionChangeFlow = MutableSharedFlow<NavxIdDecl>()

    private lateinit var tabStyleHolder: SbisTabItemStyleHolder
    private lateinit var mainTabStyleHolder: SbisTabItemStyleHolder
    internal lateinit var styleHolder: SbisTabsStyleHolder

    override var isAccent: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            mIsAccent = if (field) SbisTabInternalStyle.ACCENTED else SbisTabInternalStyle.UNACCENTED
        }

    override var style: SbisTabsStyle by delegateNotEqual(SbisTabsStyle()) { newStyle ->
        styleHolder.setStyle(tabsView.context, mIsAccent, newStyle)
        tabStyleHolder.setStyle(tabsView.context, mIsAccent, newStyle)
        itemViews.forEach { it.updateStyleHolder() }
    }

    override var tabs: LinkedList<SbisTabsViewItem> = LinkedList<SbisTabsViewItem>()
        set(value) {
            if (field == value) return
            field = value
            setItems()
            TabsPlugin.tabsController?.onTabsChanged(adapter)
        }

    override var selectedTabIndex: Int by delegateNotEqual(0) { it ->
        setSelectedTab(it)
    }

    override var isOldToolbarDesign: Boolean by delegateNotEqual(false) { it ->
        tabStyleHolder = SbisTabItemStyleHolder.create(tabsView.context, it)
        tabsViewPool.tabStyleHolder = tabStyleHolder
        itemViews.forEach { it.updateStyleHolder() }
    }
    override var isBottomBorderVisible: Boolean
        set(value) {
            tabsView.tabsContainer.isBottomBorderVisible = value
        }
        get() = tabsView.tabsContainer.isBottomBorderVisible

    override val tabSelectionFlow: Flow<NavxIdDecl>
        get() = selectionChangeFlow

    override fun setOnTabClickListener(listener: (SbisTabsViewItem) -> Unit) {
        onTabClickListener = listener
        tabsView.invalidate()
    }

    override fun hideTab(tabId: String) {
        itemViews.find { it.tabId == tabId }?.let {
            it.isVisible = false
            if (it.isSelected) {
                it.isSelected = false
                findFirstVisibleTabIndex()?.let(::setSelectedTab)
            }
        }
    }

    override fun showTab(tabId: String) {
        itemViews.find { it.tabId == tabId }?.isVisible = true
    }

    override fun hideTab(navxId: NavxIdDecl) {
        itemViews.find { it.navxId == navxId }?.let {
            it.isVisible = false
            if (it.isSelected) {
                it.isSelected = false
                findFirstVisibleTabIndex()?.let(::setSelectedTab)
            }
        }
    }

    override fun showTab(navxId: NavxIdDecl) {
        itemViews.find { it.navxId == navxId }?.isVisible = true
    }

    override fun setZenTheme(themeModel: ZenThemeModel) {
        style = SbisTabsStyle(
            customSelectedTitleColor = SbisColor(themeModel.complimentaryColor),
            customMarkerColor = SbisColor(themeModel.complimentaryColor),
            customUnselectedTitleColor = themeModel.elementsColors.defaultColor
        )
    }

    /**
     * @SelfDocumented
     */
    internal fun attach(
        view: SbisTabsView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        tabsView = view
        styleHolder = SbisTabsStyleHolder.create(view.context)
        tabStyleHolder = SbisTabItemStyleHolder.create(view.context, isOldToolbarDesign)
        mainTabStyleHolder = SbisTabItemStyleHolder.createMainTabStyleHolder(view.context)
        view.context.withStyledAttributes(attrs, R.styleable.SbisTabsView, defStyleAttr, defStyleRes) {
            loadStyle(this)
        }
        tabsContainer.setStyleHolder(styleHolder, tabStyleHolder, mainTabStyleHolder)
        tabsViewPool = TabViewPool(view.context, tabStyleHolder, mainTabStyleHolder)
    }

    /**
     * Восстановить состояние выбранности вкладки в [SbisTabsView].
     */
    internal fun restoreSelectedState(selectedItemIndex: Int) {
        tabsView.doOnNextLayout {
            setSelectedTab(selectedItemIndex)
        }
    }

    /** @SelfDocumented */
    internal fun onAttachedToWindow() {
        tabsController?.attachView(adapter)
        publishScope = CoroutineScope(SupervisorJob())
    }

    /** @SelfDocumented */
    internal fun onDetachedFromWindow() {
        publishScope.cancel()
    }

    /**
     * Возвращает смещение для элемента иконки с счетчиком, чтобы игнорировать выступ счетчика над иконкой.
     */
    internal fun iconCounterBottomMargin(content: LinkedList<SbisTabViewItemContent>) =
        if (content.singleOrNull { it is SbisTabViewItemContent.IconCounter } != null) {
            tabStyleHolder.iconCounterTopPadding
        } else {
            0
        }

    private fun loadStyle(arr: TypedArray) {
        arr.run {
            mIsAccent = loadEnum(
                R.styleable.SbisTabsView_sbisTabsView_style,
                SbisTabInternalStyle.ACCENTED,
                *SbisTabInternalStyle.values()
            )
        }
    }

    private fun setItems() {
        tabsContainer.forEach {
            if (it is SbisTabView) {
                tabsViewPool.recycle(it, it.data?.isMain ?: false)
            }
        }
        tabsContainer.removeAllViews()
        itemViews.clear()
        tabsViewPool.fullEmptyTabsId()

        tabs.forEachIndexed { index, itemModel ->
            val tabItemParams = MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            val newItemView = tabsViewPool.get(itemModel)
            itemViews.add(newItemView)
            tabsContainer.addViewWithPosition(newItemView, tabItemParams, itemModel.position)
        }

        if (tabs.isNotEmpty()) setSelectedTab(selectedTabIndex)
        setClickListeners()
        tabsView.requestLayout()
        tabsController?.onTabsChanged(adapter)
    }

    private fun setSelectedTab(tabIndex: Int) {
        if (tabIndex < 0 || itemViews.isEmpty()) return

        val trimmedTabIndex = trimIndexToTabsListSize(tabIndex)

        selectedTabIndex = trimmedTabIndex
        tabsContainer.selectedIndex = trimmedTabIndex

        itemViews.forEach { item -> item.isSelected = false }
        itemViews.getOrNull(trimmedTabIndex)?.isSelected = true

        publishScope.launch {
            tabs.getOrNull(trimmedTabIndex)?.navxId?.let {
                selectionChangeFlow.emit(it)
            }
        }
    }

    private fun trimIndexToTabsListSize(tabIndex: Int) = if (tabIndex >= itemViews.size) {
        itemViews.lastIndex
    } else {
        tabIndex
    }

    private fun setClickListeners() {
        itemViews.zip(tabs).forEachIndexed { index, viewToModel ->
            viewToModel.first.setOnClickListener {
                onTabClickListener(viewToModel.second)
                setSelectedTab(index)
                tabsView.invalidate()
            }
        }
    }

    private fun findFirstVisibleTabIndex(): Int? =
        tabs.indices.firstOrNull { itemViews[it].isVisible }
}