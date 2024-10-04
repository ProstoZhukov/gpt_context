package ru.tensor.sbis.design.tab_panel

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.ThemeContextBuilder

typealias ClickItemHandler = (item: TabPanelItem) -> Unit

/**
 * Панель вкладок
 *
 * @author ai.abramenko
 */
class TabPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.tabPanelTheme,
    @StyleRes defStyleRes: Int = R.style.TabPanelDefaultTheme
) : HorizontalScrollView(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private val tabPanelContainerView = TabPanelContainerView(context)
    @Px
    private val tabPanelHeight: Int = resources.getDimensionPixelSize(R.dimen.design_tab_panel_height)

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.TabPanelView,
            R.attr.tabPanelTheme,
            R.style.TabPanelDefaultTheme
        ) {
            val titleMaxWidth = getDimensionPixelSize(
                R.styleable.TabPanelView_TabPanelView_itemTitleMaxWidth,
                -1
            ).takeIf { it != -1 }
            tabPanelContainerView.customTitleMaxWidth = titleMaxWidth
        }

        isHorizontalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        id = R.id.design_tab_panel_view_id
        tabPanelContainerView.id = R.id.design_tab_panel_container_view_id
        addView(tabPanelContainerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    /**
     * Установить список вкладок
     */
    fun setTabPanelItems(items: List<TabPanelItem>) {
        tabPanelContainerView.setTabPanelItems(items)
    }

    /**
     * Установить выбранную вкладку.
     * Должно вызываться после [setTabPanelItems].
     * Переданный [item] должен содержаться в списке переданном в [setTabPanelItems].
     * При установке выбранной вкладки через данный метод, [ClickItemHandler] вызван не будет.
     */
    fun setSelectedItem(item: TabPanelItem) {
        tabPanelContainerView.setSelectedItem(item)
    }

    /**
     * Установить слушатель клика по вкладке.
     * Если вкладка уже выбрана, то [ClickItemHandler] вызван не будет.
     */
    fun setClickItemHandler(handler: ClickItemHandler) {
        tabPanelContainerView.clickItemHandler = handler
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val horizontalPadding: Int = Offset.L.getDimenPx(context)
        val layoutSize: Int = MeasureSpec.getSize(widthMeasureSpec) - horizontalPadding
        val minTabWidth: Int = resources.getDimensionPixelSize(R.dimen.design_tab_panel_item_size) + horizontalPadding
        val maxTabsCount = layoutSize / minTabWidth
        val currentTabsCount = tabPanelContainerView.childCount

        if ((currentTabsCount) > maxTabsCount) {
            tabPanelContainerView.horizontalPadding = horizontalPadding / 2
            tabPanelContainerView.tabWidth = minTabWidth
        } else {
            if (currentTabsCount > 0) {
                tabPanelContainerView.tabWidth = (layoutSize + horizontalPadding) / currentTabsCount
            } else {
                tabPanelContainerView.tabWidth = 0
            }
        }

        val tabNavViewHeightMeasureSpec = MeasureSpecUtils.makeExactlySpec(tabPanelHeight)
        measureChild(tabPanelContainerView, widthMeasureSpec, tabNavViewHeightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, tabNavViewHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        tabPanelContainerView.layout(0, 0, tabPanelContainerView.measuredWidth, tabPanelHeight)
    }
}