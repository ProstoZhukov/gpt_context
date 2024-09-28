package ru.tensor.sbis.design.tab_panel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.children
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import timber.log.Timber

/**
 * Представление для отображения списка вкладок
 *
 * @author ai.abramenko
 */
internal class TabPanelContainerView(context: Context) : ViewGroup(context) {

    /**
     * Ширина каждого элемента списка.
     */
    var tabWidth = 0

    /**
     * Горизонтальные отступы, если элементов в списке больше, чем помещаются на экран.
     */
    var horizontalPadding = 0

    var clickItemHandler: ClickItemHandler? = null

    @Px
    var customTitleMaxWidth: Int? = null

    private var selectedView: TabPanelItemView? = null

    fun setTabPanelItems(items: List<TabPanelItem>) {
        removeAllViewsInLayout()
        items.forEach {
            addView(createView(it))
        }
    }

    fun setSelectedItem(item: TabPanelItem) {
        if (item.isUnmarked) {
            Timber.d("Cannot select because TabPanelItem is unmarked.")
            return
        }
        findView(item)?.let { select(it) }
            ?: illegalState { "View for this TabPanelItem not found." }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = horizontalPadding
        children.forEach {
            measureChild(
                it,
                if (tabWidth != 0) {
                    MeasureSpecUtils.makeAtMostSpec(tabWidth)
                } else {
                    MeasureSpecUtils.makeUnspecifiedSpec()
                },
                heightMeasureSpec
            )
            width += it.measuredWidth
        }
        width += horizontalPadding
        val widthSpec = MeasureSpecUtils.makeExactlySpec(width)
        super.onMeasure(widthSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var offset = horizontalPadding
        children.forEach {
            it.layout(offset, 0)
            offset = it.right
        }
    }

    private fun createView(item: TabPanelItem): TabPanelItemView =
        TabPanelItemView(context).also { view ->
            customTitleMaxWidth?.also(view::setTitleMaxWidth)
            view.applyItem(item)
            view.itemId = item.id
            view.id = R.id.design_tab_panel_item_view_id
            view.preventDoubleClickListener {
                if (item.isUnmarked) {
                    clickItemHandler?.invoke(item)
                } else if (!view.isSelected) {
                    select(view)
                    clickItemHandler?.invoke(item)
                }
            }
        }

    private fun findView(item: TabPanelItem): TabPanelItemView? =
        children.find { it.itemId == item.id } as? TabPanelItemView

    private fun select(view: TabPanelItemView) {
        if (selectedView == view && view.isSelected) {
            return
        }
        selectedView?.isSelected = false
        view.isSelected = true
        selectedView = view
    }

    private var View.itemId: String
        get() = tag as String
        set(value) { tag = value }
}